package com.handymanhub.dto.response;

import com.handymanhub.model.Booking;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingResponseDto {

    private Long id;
    private Long customerId;
    private String customerName;
    private Long workerId;
    private String workerName;
    private Long contractorId;
    private String contractorName;
    private Long skillId;
    private String skillName;
    private LocalDate scheduledDate;
    private Integer durationDays;
    private Booking.Status status;
    private String address;
    private String notes;
    private LocalDateTime createdAt;

    private BookingResponseDto() {}

    public Long getId()                  { return id; }
    public Long getCustomerId()          { return customerId; }
    public String getCustomerName()      { return customerName; }
    public Long getWorkerId()            { return workerId; }
    public String getWorkerName()        { return workerName; }
    public Long getContractorId()        { return contractorId; }
    public String getContractorName()    { return contractorName; }
    public Long getSkillId()             { return skillId; }
    public String getSkillName()         { return skillName; }
    public LocalDate getScheduledDate()  { return scheduledDate; }
    public Integer getDurationDays()     { return durationDays; }
    public Booking.Status getStatus()    { return status; }
    public String getAddress()           { return address; }
    public String getNotes()             { return notes; }
    public LocalDateTime getCreatedAt()  { return createdAt; }

    public static Builder builder()      { return new Builder(); }

    public static class Builder {
        private final BookingResponseDto dto = new BookingResponseDto();

        public Builder id(Long v)                  { dto.id = v; return this; }
        public Builder customerId(Long v)          { dto.customerId = v; return this; }
        public Builder customerName(String v)      { dto.customerName = v; return this; }
        public Builder workerId(Long v)            { dto.workerId = v; return this; }
        public Builder workerName(String v)        { dto.workerName = v; return this; }
        public Builder contractorId(Long v)        { dto.contractorId = v; return this; }
        public Builder contractorName(String v)    { dto.contractorName = v; return this; }
        public Builder skillId(Long v)             { dto.skillId = v; return this; }
        public Builder skillName(String v)         { dto.skillName = v; return this; }
        public Builder scheduledDate(LocalDate v)  { dto.scheduledDate = v; return this; }
        public Builder durationDays(Integer v)     { dto.durationDays = v; return this; }
        public Builder status(Booking.Status v)    { dto.status = v; return this; }
        public Builder address(String v)           { dto.address = v; return this; }
        public Builder notes(String v)             { dto.notes = v; return this; }
        public Builder createdAt(LocalDateTime v)  { dto.createdAt = v; return this; }

        public BookingResponseDto build()          { return dto; }
    }
}