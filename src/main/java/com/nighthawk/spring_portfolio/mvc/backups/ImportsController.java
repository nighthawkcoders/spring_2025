package com.nighthawk.spring_portfolio.mvc.backups;

import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

@Component
@RestController
@RequestMapping("/api/imports/")
public class ImportsController {

    private static final String DB_PATH = "./volumes/sqlite.db";
    private static final String BACKUP_DIR = "./volumes/backups/";

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Automatically import from the most recent backup on application startup
    @EventListener(ApplicationReadyEvent.class)
    public void importFromMostRecentBackup() {
        File mostRecentBackup = getMostRecentBackupFile();
        if (mostRecentBackup != null) {
            importFromFile(mostRecentBackup);
        } else {
            System.out.println("No backup files found to import.");
        }
    }

    @PostMapping("/manual")
    public String manualImport(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "No file uploaded.";
        }

        try {
            // Import data directly from the uploaded file
            String result = importFromMultipartFile(file);

            // Manage backups to keep only the three most recent ones
            manageBackups();

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to process the uploaded file: " + e.getMessage();
        }
    }

    // Helper method to import data directly from a MultipartFile
    private String importFromMultipartFile(MultipartFile file) {
        try {
            // Parse the JSON content directly from the MultipartFile
            Map<String, List<Map<String, Object>>> data = objectMapper.readValue(file.getInputStream(), Map.class);

            try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH)) {
                for (Map.Entry<String, List<Map<String, Object>>> entry : data.entrySet()) {
                    String tableName = entry.getKey();
                    List<Map<String, Object>> tableData = entry.getValue();
                    insertTableData(connection, tableName, tableData);
                }
            }

            return "Data imported successfully from uploaded file: " + file.getOriginalFilename();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to import data: " + e.getMessage();
        }
    }

    // Helper method to import data from a JSON file
    private String importFromFile(File jsonFile) {
        try {
            Map<String, List<Map<String, Object>>> data = objectMapper.readValue(jsonFile, Map.class);

            try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH)) {
                for (Map.Entry<String, List<Map<String, Object>>> entry : data.entrySet()) {
                    String tableName = entry.getKey();
                    List<Map<String, Object>> tableData = entry.getValue();
                    insertTableData(connection, tableName, tableData);
                }
            }

            return "Data imported successfully from JSON file: " + jsonFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to import data: " + e.getMessage();
        }
    }

    // Helper method to find the most recent backup file
    private File getMostRecentBackupFile() {
        File backupsDir = new File(BACKUP_DIR);
        if (!backupsDir.exists() || !backupsDir.isDirectory()) {
            return null;
        }

        File[] backupFiles = backupsDir.listFiles((dir, name) -> name.startsWith("backup_") && name.endsWith(".json"));
        if (backupFiles == null || backupFiles.length == 0) {
            return null;
        }

        // Sort files by last modified date (most recent first)
        Arrays.sort(backupFiles, Comparator.comparingLong(File::lastModified).reversed());

        return backupFiles[0]; // Return the most recent file
    }

    // Helper method to insert data into a table
    private void insertTableData(Connection connection, String tableName, List<Map<String, Object>> tableData) throws SQLException {
        if (tableData.isEmpty()) {
            return;
        }

        Set<String> columns = tableData.get(0).keySet();
        String sql = buildInsertQuery(tableName, columns);

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (Map<String, Object> row : tableData) {
                if (!isRowExists(connection, tableName, row)) {
                    int index = 1;
                    for (String column : columns) {
                        preparedStatement.setObject(index++, row.get(column));
                    }
                    preparedStatement.addBatch();
                }
            }

            try {
                preparedStatement.executeBatch();
            } catch (SQLException e) {
                System.err.println("Skipping duplicate entries: " + e.getMessage());
            }
        }
    }

    // Helper method to check if a row already exists in the table
    private boolean isRowExists(Connection connection, String tableName, Map<String, Object> row) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder("SELECT 1 FROM " + tableName + " WHERE ");
        List<Object> values = new ArrayList<>();

        for (Map.Entry<String, Object> entry : row.entrySet()) {
            queryBuilder.append(entry.getKey()).append(" = ? AND ");
            values.add(entry.getValue());
        }

        queryBuilder.delete(queryBuilder.length() - 5, queryBuilder.length()); // Remove the last " AND "

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())) {
            int index = 1;
            for (Object value : values) {
                preparedStatement.setObject(index++, value);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    // Helper method to build the INSERT query
    private String buildInsertQuery(String tableName, Set<String> columns) {
        StringBuilder columnsBuilder = new StringBuilder();
        StringBuilder valuesBuilder = new StringBuilder();

        for (String column : columns) {
            columnsBuilder.append(column).append(",");
            valuesBuilder.append("?,");
        }

        columnsBuilder.deleteCharAt(columnsBuilder.length() - 1);
        valuesBuilder.deleteCharAt(valuesBuilder.length() - 1);

        return "INSERT OR IGNORE INTO " + tableName + " (" + columnsBuilder + ") VALUES (" + valuesBuilder + ")";
    }

     private void manageBackups() {
        File backupsDir = new File(BACKUP_DIR);
        if (!backupsDir.exists() || !backupsDir.isDirectory()) {
            return;
        }

        // Get all backup files
        File[] backupFiles = backupsDir.listFiles((dir, name) -> name.startsWith("backup_") && name.endsWith(".json"));

        if (backupFiles == null || backupFiles.length <= 3) {
            return;
        }

        // Sort files by last modified date (oldest first)
        Arrays.sort(backupFiles, Comparator.comparingLong(File::lastModified));

        // Delete the oldest files if there are more than three
        for (int i = 0; i < backupFiles.length - 3; i++) {
            if (backupFiles[i].delete()) {
                System.out.println("Deleted old backup: " + backupFiles[i].getName());
            } else {
                System.out.println("Failed to delete old backup: " + backupFiles[i].getName());
            }
        }
    }
}