package com.handymanhub.dto.response;

import java.time.LocalDateTime;

public class CustomerResponseDto {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String pincode;
    private LocalDateTime createdAt;

    private CustomerResponseDto() {}

    public Long getId()                  { return id; }
    public String getName()              { return name; }
    public String getPhone()             { return phone; }
    public String getEmail()             { return email; }
    public String getAddress()           { return address; }
    public String getPincode()           { return pincode; }
    public LocalDateTime getCreatedAt()  { return createdAt; }

    public static Builder builder()      { return new Builder(); }

    public static class Builder {
        private final CustomerResponseDto dto = new CustomerResponseDto();

        public Builder id(Long v)                { dto.id = v; return this; }
        public Builder name(String v)            { dto.name = v; return this; }
        public Builder phone(String v)           { dto.phone = v; return this; }
        public Builder email(String v)           { dto.email = v; return this; }
        public Builder address(String v)         { dto.address = v; return this; }
        public Builder pincode(String v)         { dto.pincode = v; return this; }
        public Builder createdAt(LocalDateTime v){ dto.createdAt = v; return this; }

        public CustomerResponseDto build()       { return dto; }
    }
}