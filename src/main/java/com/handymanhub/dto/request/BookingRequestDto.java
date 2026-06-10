package com.handymanhub.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class BookingRequestDto {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private Long workerId;

    private Long contractorId;

    @NotNull(message = "Skill ID is required")
    private Long skillId;

    @NotNull(message = "Scheduled date is required")
    @FutureOrPresent(message = "Scheduled date cannot be in the past")
    private LocalDate scheduledDate;

    @Min(value = 1, message = "Duration must be at least 1 day")
    @Max(value = 365, message = "Duration cannot exceed 365 days")
    private Integer durationDays = 1;

    private String address;
    private String notes;

    public Long getCustomerId()                  { return customerId; }
    public void setCustomerId(Long v)            { this.customerId = v; }

    public Long getWorkerId()                    { return workerId; }
    public void setWorkerId(Long v)              { this.workerId = v; }

    public Long getContractorId()                { return contractorId; }
    public void setContractorId(Long v)          { this.contractorId = v; }

    public Long getSkillId()                     { return skillId; }
    public void setSkillId(Long v)               { this.skillId = v; }

    public LocalDate getScheduledDate()          { return scheduledDate; }
    public void setScheduledDate(LocalDate v)    { this.scheduledDate = v; }

    public Integer getDurationDays()             { return durationDays; }
    public void setDurationDays(Integer v)       { this.durationDays = v; }

    public String getAddress()                   { return address; }
    public void setAddress(String v)             { this.address = v; }

    public String getNotes()                     { return notes; }
    public void setNotes(String v)               { this.notes = v; }
}