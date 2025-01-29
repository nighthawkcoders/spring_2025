package com.nighthawk.spring_portfolio.mvc.mortevision.stream;

import lombok.Data;

@Data
public class iceData {
    private String candidate;

    public void setCandidate(String candidate)
    {
        this.candidate = candidate;
    }
}