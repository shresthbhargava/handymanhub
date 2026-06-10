package com.handymanhub.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contractors")
public class Contractor {
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 15)
    private String phone;

    @Column(unique = true, length = 150)
    private String email;

    @Column(length = 10)
    private String pincode;

    @Column(name = "company_name", length = 150)
    private String companyName;

    @Column(nullable = false)
    private Boolean verified = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "contractor", fetch = FetchType.LAZY)
    private List<Worker> workers = new ArrayList<>();

    public Contractor() {}

    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }

    public String getName()                    { return name; }
    public void setName(String name)           { this.name = name; }

    public String getPhone()                   { return phone; }
    public void setPhone(String phone)         { this.phone = phone; }

    public String getEmail()                   { return email; }
    public void setEmail(String email)         { this.email = email; }

    public String getPincode()                 { return pincode; }
    public void setPincode(String pincode)     { this.pincode = pincode; }

    public String getCompanyName()             { return companyName; }
    public void setCompanyName(String name)    { this.companyName = name; }

    public Boolean getVerified()               { return verified; }
    public void setVerified(Boolean verified)  { this.verified = verified; }

    public LocalDateTime getCreatedAt()        { return createdAt; }

    public List<Worker> getWorkers()           { return workers; }
    public void setWorkers(List<Worker> workers){ this.workers = workers; }

    public static Builder builder()            { return new Builder(); }

    public static class Builder {
        private String name, phone, email, pincode, companyName;
        private Boolean verified = false;

        public Builder name(String name)           { this.name = name; return this; }
        public Builder phone(String phone)         { this.phone = phone; return this; }
        public Builder email(String email)         { this.email = email; return this; }
        public Builder pincode(String pincode)     { this.pincode = pincode; return this; }
        public Builder companyName(String cn)      { this.companyName = cn; return this; }
        public Builder verified(Boolean v)         { this.verified = v; return this; }

        public Contractor build() {
            Contractor c = new Contractor();
            c.name = this.name;
            c.phone = this.phone;
            c.email = this.email;
            c.pincode = this.pincode;
            c.companyName = this.companyName;
            c.verified = this.verified;
            return c;
        }
    }
}