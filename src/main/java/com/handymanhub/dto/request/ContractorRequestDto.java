package com.handymanhub.dto.request;

import jakarta.validation.constraints.*;

public class ContractorRequestDto {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian mobile number")
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^\\d{6}$", message = "Pincode must be 6 digits")
    private String pincode;

    @Size(max = 150, message = "Company name cannot exceed 150 characters")
    private String companyName;

    public String getName()                  { return name; }
    public void setName(String name)         { this.name = name; }

    public String getPhone()                 { return phone; }
    public void setPhone(String phone)       { this.phone = phone; }

    public String getEmail()                 { return email; }
    public void setEmail(String email)       { this.email = email; }

    public String getPincode()               { return pincode; }
    public void setPincode(String pincode)   { this.pincode = pincode; }

    public String getCompanyName()           { return companyName; }
    public void setCompanyName(String cn)    { this.companyName = cn; }
}