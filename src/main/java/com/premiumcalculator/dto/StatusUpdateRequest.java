package com.premiumcalculator.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for approving or rejecting a transaction.
 */
public class StatusUpdateRequest {

    @NotBlank(message = "New status is required (APPROVED or REJECTED).")
    private String status;

    private String notes;

    public StatusUpdateRequest() {
    }

    public StatusUpdateRequest(String status) {
        this.status = status;
    }

    public StatusUpdateRequest(String status, String notes) {
        this.status = status;
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
