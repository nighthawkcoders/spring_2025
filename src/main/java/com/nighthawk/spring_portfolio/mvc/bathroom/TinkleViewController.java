package com.nighthawk.spring_portfolio.mvc.bathroom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class TinkleViewController {

    @Autowired
    private TinkleJPARepository tinkleRepository;

    @Autowired
    private TinkleStatisticsService tinkleStatisticsService;

    @GetMapping("/admin/tinkle-view")
    public String showTinkleViewWithDurations(Model model) {
        List<Tinkle> tinkleList = tinkleRepository.findAll();

        // Add durations for each entry
        List<Map<String, String>> tinkleDataWithDurations = tinkleList.stream().map(tinkle -> {
            Map<String, String> row = new HashMap<>();
            row.put("person_name", tinkle.getPerson_name());
            row.put("timeIn", tinkle.getTimeIn());
            row.put("duration", tinkleStatisticsService.calculateDurationFormatted(tinkle.getTimeIn()));
            return row;
        }).collect(Collectors.toList());
        
        tinkleDataWithDurations.forEach(row -> {
            System.out.println("Row: " + row);
        });
        
        // Calculate average weekly durations
        Map<String, String> averageWeeklyDurations = tinkleStatisticsService.calculateAverageWeeklyDurationsFormatted(tinkleList);

        model.addAttribute("tinkleList", tinkleDataWithDurations); // Updated list with durations
        model.addAttribute("averageWeeklyDurations", averageWeeklyDurations);

        return "tinkle-view";
    }

}
