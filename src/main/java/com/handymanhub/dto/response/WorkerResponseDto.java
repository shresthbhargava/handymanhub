package com.handymanhub.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WorkerResponseDto {

    private Long id;
    private String name;
    private String phone;
    private String pincode;
    private BigDecimal dailyRate;
    private Boolean available;
    private Long contractorId;
    private String contractorName;
    private LocalDateTime createdAt;

    private WorkerResponseDto() {}

    public Long getId()                  { return id; }
    public String getName()              { return name; }
    public String getPhone()             { return phone; }
    public String getPincode()           { return pincode; }
    public BigDecimal getDailyRate()     { return dailyRate; }
    public Boolean getAvailable()        { return available; }
    public Long getContractorId()        { return contractorId; }
    public String getContractorName()    { return contractorName; }
    public LocalDateTime getCreatedAt()  { return createdAt; }

    public static Builder builder()      { return new Builder(); }

    public static class Builder {
        private final WorkerResponseDto dto = new WorkerResponseDto();

        public Builder id(Long v)                { dto.id = v; return this; }
        public Builder name(String v)            { dto.name = v; return this; }
        public Builder phone(String v)           { dto.phone = v; return this; }
        public Builder pincode(String v)         { dto.pincode = v; return this; }
        public Builder dailyRate(BigDecimal v)   { dto.dailyRate = v; return this; }
        public Builder available(Boolean v)      { dto.available = v; return this; }
        public Builder contractorId(Long v)      { dto.contractorId = v; return this; }
        public Builder contractorName(String v)  { dto.contractorName = v; return this; }
        public Builder createdAt(LocalDateTime v){ dto.createdAt = v; return this; }

        public WorkerResponseDto build()         { return dto; }
    }
}