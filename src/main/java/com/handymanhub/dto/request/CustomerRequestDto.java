package com.handymanhub.dto.request;

import jakarta.validation.constraints.*;

public class CustomerRequestDto {

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian mobile number")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String address;

    @Pattern(regexp = "^\\d{6}$", message = "Pincode must be 6 digits")
    private String pincode;

    public String getName()                { return name; }
    public void setName(String v)          { this.name = v; }
    public String getPhone()               { return phone; }
    public void setPhone(String v)         { this.phone = v; }
    public String getEmail()               { return email; }
    public void setEmail(String v)         { this.email = v; }
    public String getAddress()             { return address; }
    public void setAddress(String v)       { this.address = v; }
    public String getPincode()             { return pincode; }
    public void setPincode(String v)       { this.pincode = v; }
}