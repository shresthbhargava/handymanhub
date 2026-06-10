package com.handymanhub.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class WorkerRequestDto {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian mobile number")
    private String phone;

    @Pattern(regexp = "^\\d{6}$", message = "Pincode must be 6 digits")
    private String pincode;

    @DecimalMin(value = "0.0", inclusive = false, message = "Daily rate must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Invalid daily rate format")
    private BigDecimal dailyRate;

    private Long contractorId;

    public String getName()                  { return name; }
    public void setName(String name)         { this.name = name; }

    public String getPhone()                 { return phone; }
    public void setPhone(String phone)       { this.phone = phone; }

    public String getPincode()               { return pincode; }
    public void setPincode(String pincode)   { this.pincode = pincode; }

    public BigDecimal getDailyRate()         { return dailyRate; }
    public void setDailyRate(BigDecimal r)   { this.dailyRate = r; }

    public Long getContractorId()            { return contractorId; }
    public void setContractorId(Long id)     { this.contractorId = id; }
}