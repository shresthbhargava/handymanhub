package com.handymanhub.dto.response;

import java.time.LocalDateTime;

public class ContractorResponseDto {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private String pincode;
    private String companyName;
    private Boolean verified;
    private int workerCount;
    private LocalDateTime createdAt;

    private ContractorResponseDto() {}

    public Long getId()                 { return id; }
    public String getName()             { return name; }
    public String getPhone()            { return phone; }
    public String getEmail()            { return email; }
    public String getPincode()          { return pincode; }
    public String getCompanyName()      { return companyName; }
    public Boolean getVerified()        { return verified; }
    public int getWorkerCount()         { return workerCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static Builder builder()     { return new Builder(); }

    public static class Builder {
        private final ContractorResponseDto dto = new ContractorResponseDto();

        public Builder id(Long v)                { dto.id = v; return this; }
        public Builder name(String v)            { dto.name = v; return this; }
        public Builder phone(String v)           { dto.phone = v; return this; }
        public Builder email(String v)           { dto.email = v; return this; }
        public Builder pincode(String v)         { dto.pincode = v; return this; }
        public Builder companyName(String v)     { dto.companyName = v; return this; }
        public Builder verified(Boolean v)       { dto.verified = v; return this; }
        public Builder workerCount(int v)        { dto.workerCount = v; return this; }
        public Builder createdAt(LocalDateTime v){ dto.createdAt = v; return this; }

        public ContractorResponseDto build()     { return dto; }
    }
}