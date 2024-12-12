package com.nighthawk.spring_portfolio.mvc.bathroom;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TinkleStatisticsService {

    /**
     * Calculates the average weekly duration for each user.
     *
     * @param tinkleList List of Tinkle entries from the database.
     * @return A map where the key is the user's name and the value is their average weekly duration in seconds.
     */
    public Map<String, Long> calculateAverageWeeklyDurations(List<Tinkle> tinkleList) {
        // Group durations by person name
        Map<String, List<Long>> userWeeklyDurations = tinkleList.stream()
            .collect(Collectors.groupingBy(
                Tinkle::getPerson_name,
                Collectors.mapping(
                    t -> calculateTotalDurationInSeconds(t.getTimeIn()),
                    Collectors.toList()
                )
            ));

        // Calculate the average weekly duration per user
        Map<String, Long> averageWeeklyDurations = new HashMap<>();
        for (Map.Entry<String, List<Long>> entry : userWeeklyDurations.entrySet()) {
            String userName = entry.getKey();
            List<Long> durations = entry.getValue();

            // Sum all durations and divide by the number of weeks (assuming 1 entry per week)
            long totalDuration = durations.stream().mapToLong(Long::longValue).sum();
            long averageDuration = totalDuration / durations.size();
            averageWeeklyDurations.put(userName, averageDuration);
        }

        return averageWeeklyDurations;
    }

    /**
     * Calculates the total duration in seconds for a given timeIn string.
     *
     * @param timeIn A string containing time pairs (e.g., "08:00:00-08:10:00,10:30:00-10:45:00").
     * @return Total duration in seconds.
     */
    public long calculateTotalDurationInSeconds(String timeIn) {
        if (timeIn == null || timeIn.isEmpty()) return 0;
    
        return Arrays.stream(timeIn.split(","))
            .mapToLong(pair -> {
                String[] times = pair.split("-");
                if (times.length == 2) {
                    try {
                        return Duration.between(LocalTime.parse(times[0]), LocalTime.parse(times[1])).getSeconds();
                    } catch (Exception e) {
                        return 0; // Skip malformed entries
                    }
                }
                return 0;
            })
            .sum();
    }

    public String formatDuration(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    public Map<String, String> calculateAverageWeeklyDurationsFormatted(List<Tinkle> tinkleList) {
        Map<String, Long> averageWeeklyDurations = calculateAverageWeeklyDurations(tinkleList);
        return averageWeeklyDurations.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> formatDuration(entry.getValue())
            ));
    }
    
    public String calculateDurationFormatted(String timeIn) {
        long totalSeconds = calculateTotalDurationInSeconds(timeIn);
        return formatDuration(totalSeconds); // Reuse the formatDuration method
    }
    
    
}
