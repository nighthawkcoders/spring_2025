package com.nighthawk.spring_portfolio.mvc.backups;

import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.sqlite.SQLiteException;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RestController
@RequestMapping("/api/imports/")
public class ImportsController {

    private static final String DB_PATH = "./volumes/sqlite.db";
    private static final String BACKUP_DIR = "./volumes/backups/";

    private final ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    private final Object lock = new Object();

    @EventListener(ApplicationReadyEvent.class)
    public synchronized void importFromMostRecentBackup() {
        File mostRecentBackup = getMostRecentBackupFile();
        if (mostRecentBackup != null) {
            System.out.println("Importing from backup: " + mostRecentBackup.getName());
            String result = importFromFile(mostRecentBackup);
            System.out.println(result);
            System.out.println(mostRecentBackup);
        } else {
            System.out.println("No backup files found to import.");
        }
    }

    @PostMapping("/manual")
    public String manualImport(@RequestParam("file") MultipartFile file) {
        synchronized (lock) {
            if (file.isEmpty()) {
                return "No file uploaded.";
            }

            try {
                String result = importFromMultipartFile(file);
                manageBackups();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed to process the uploaded file: " + e.getMessage();
            }
        }
    }

    public String importFromMultipartFile(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            String rawJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("Raw JSON Content:\n" + rawJson);

            Map<String, List<Map<String, Object>>> data = objectMapper.readValue(rawJson, Map.class);
            System.out.println("Parsed data: " + data);

            sanitizeAndProcessData(data);
            return "Data imported successfully from uploaded file: " + file.getOriginalFilename();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to import data: " + e.getMessage();
        }
    }

    private String importFromFile(File jsonFile) {
        int retries = 3;
        while (retries > 0) {
            try {
                String rawJson = new String(Files.readAllBytes(jsonFile.toPath()));
                System.out.println("Raw JSON content: " + rawJson);

                objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
                objectMapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

                BackupData backupData = objectMapper.readValue(rawJson, BackupData.class);
                Map<String, List<Map<String, Object>>> data = backupData.getTables();
                System.out.println("Parsed data: " + data);

                sanitizeAndProcessData(data);
                return "Data imported successfully from JSON file: " + jsonFile.getAbsolutePath();
            } catch (SQLiteException e) {
                if (e.getMessage().contains("database is locked")) {
                    retries--;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return "Thread interrupted while waiting to retry.";
                    }
                } else {
                    e.printStackTrace();
                    return "Failed to import data: " + e.getMessage();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed to import data: " + e.getMessage();
            }
        }
        return "Failed to acquire database lock after retries.";
    }

    public static class BackupData {
        private Map<String, List<Map<String, Object>>> tables = new HashMap<>();

        @JsonAnySetter
        public void set(String key, Object value) {
            if (value instanceof List) {
                tables.put(key, (List<Map<String, Object>>) value);
            } else {
                throw new IllegalArgumentException("Expected a List<Map<String, Object>> for key: " + key);
            }
        }

        public Map<String, List<Map<String, Object>>> getTables() {
            return tables;
        }

        public void setTables(Map<String, List<Map<String, Object>>> tables) {
            this.tables = tables;
        }
    }

    private void enableWalMode(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA journal_mode=WAL;");
        }
    }

    private void setBusyTimeout(Connection connection, int timeoutMillis) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA busy_timeout=" + timeoutMillis + ";");
        }
    }

    private void sanitizeAndProcessData(Map<String, List<Map<String, Object>>> data) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH)) {
            enableWalMode(connection);
            setBusyTimeout(connection, 30000); // 30 seconds
            for (Map.Entry<String, List<Map<String, Object>>> entry : data.entrySet()) {
                String tableName = sanitizeTableName(entry.getKey());
                List<Map<String, Object>> tableData = entry.getValue();
                ensureTableExists(connection, tableName, tableData);
                insertTableData(connection, tableName, tableData);
            }
        }
    }

    private String sanitizeTableName(String tableName) {
        return tableName.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    private File getMostRecentBackupFile() {
        File backupsDir = new File(BACKUP_DIR);
        if (!backupsDir.exists() || !backupsDir.isDirectory()) {
            return null;
        }

        File[] backupFiles = backupsDir.listFiles((dir, name) -> name.startsWith("backup_") && name.endsWith(".json"));
        if (backupFiles == null || backupFiles.length == 0) {
            return null;
        }

        Arrays.sort(backupFiles, Comparator.comparingLong(File::lastModified).reversed());
        return backupFiles[0];
    }

    private void insertTableData(Connection connection, String tableName, List<Map<String, Object>> tableData) throws SQLException {
        if (tableData.isEmpty()) {
            return;
        }

        Set<String> columns = tableData.get(0).keySet();
        String sql = buildUpsertQuery(tableName, columns);

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (Map<String, Object> row : tableData) {
                int index = 1;
                for (String column : columns) {
                    preparedStatement.setObject(index++, row.get(column));
                }
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        }
    }

    private String buildUpsertQuery(String tableName, Set<String> columns) {
        String columnList = String.join(", ", columns);
        String valuePlaceholders = String.join(", ", Collections.nCopies(columns.size(), "?"));

        return "INSERT OR REPLACE INTO " + tableName + " (" + columnList + ") VALUES (" + valuePlaceholders + ")";
    }

    private void ensureTableExists(Connection connection, String tableName, List<Map<String, Object>> tableData) throws SQLException {
        if (tableData.isEmpty()) {
            return;
        }

        Set<String> columns = tableData.get(0).keySet();
        if (!tableExists(connection, tableName)) {
            createTable(connection, tableName, columns);
        } else {
            for (String column : columns) {
                if (!columnExists(connection, tableName, column)) {
                    addColumn(connection, tableName, column);
                }
            }
        }
    }

    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet resultSet = meta.getTables(null, null, tableName, null)) {
            return resultSet.next();
        }
    }

    private void createTable(Connection connection, String tableName, Set<String> columns) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("CREATE TABLE " + tableName + " (");
        for (String column : columns) {
            if (column.equalsIgnoreCase("id")) {
                sqlBuilder.append(column).append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
            } else {
                sqlBuilder.append(column).append(" TEXT,");
            }
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(")");

        try (Statement statement = connection.createStatement()) {
            statement.execute(sqlBuilder.toString());
        }
    }

    private boolean columnExists(Connection connection, String tableName, String columnName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet resultSet = meta.getColumns(null, null, tableName, columnName)) {
            return resultSet.next();
        }
    }

    private void addColumn(Connection connection, String tableName, String columnName) throws SQLException {
        String sql = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " TEXT";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private void manageBackups() {
        File backupsDir = new File(BACKUP_DIR);
        if (!backupsDir.exists() || !backupsDir.isDirectory()) {
            return;
        }

        File[] backupFiles = backupsDir.listFiles((dir, name) -> name.startsWith("backup_") && name.endsWith(".json"));

        if (backupFiles == null || backupFiles.length <= 3) {
            return;
        }

        Arrays.sort(backupFiles, Comparator.comparingLong(File::lastModified));

        for (int i = 0; i < backupFiles.length - 3; i++) {
            if (backupFiles[i].delete()) {
                System.out.println("Deleted old backup: " + backupFiles[i].getName());
            } else {
                System.out.println("Failed to delete old backup: " + backupFiles[i].getName());
            }
        }
    }
}