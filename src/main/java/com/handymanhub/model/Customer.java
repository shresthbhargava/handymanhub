package com.handymanhub.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, length = 15)
    private String phone;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 10)
    private String pincode;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Customer() {}

    public Long getId()                    { return id; }
    public void setId(Long id)             { this.id = id; }

    public String getName()                { return name; }
    public void setName(String name)       { this.name = name; }

    public String getPhone()               { return phone; }
    public void setPhone(String phone)     { this.phone = phone; }

    public String getEmail()               { return email; }
    public void setEmail(String email)     { this.email = email; }

    public String getAddress()             { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPincode()             { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public LocalDateTime getCreatedAt()    { return createdAt; }

    public static Builder builder()        { return new Builder(); }

    public static class Builder {
        private String name, phone, email, address, pincode;

        public Builder name(String v)      { this.name = v; return this; }
        public Builder phone(String v)     { this.phone = v; return this; }
        public Builder email(String v)     { this.email = v; return this; }
        public Builder address(String v)   { this.address = v; return this; }
        public Builder pincode(String v)   { this.pincode = v; return this; }

        public Customer build() {
            Customer c = new Customer();
            c.name = this.name;
            c.phone = this.phone;
            c.email = this.email;
            c.address = this.address;
            c.pincode = this.pincode;
            return c;
        }
    }
}