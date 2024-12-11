package com.nighthawk.spring_portfolio.mvc.synergy;

/**
 * A data transfer object that stores the id of a grade request.
 */
public class SynergyGradeRequestIdDTO {
    private Long requestId;

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getRequestId() {
        return this.requestId;
    }
}