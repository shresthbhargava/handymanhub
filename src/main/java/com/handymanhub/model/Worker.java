package com.handymanhub.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workers")
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 15)
    private String phone;

    @Column(length = 10)
    private String pincode;

    @Column(name = "daily_rate", precision = 10, scale = 2)
    private BigDecimal dailyRate;

    @Column(nullable = false)
    private Boolean available = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contractor_id")
    private Contractor contractor;

    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkerSkill> workerSkills = new ArrayList<>();

    public Worker() {}

    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }

    public String getName()                    { return name; }
    public void setName(String name)           { this.name = name; }

    public String getPhone()                   { return phone; }
    public void setPhone(String phone)         { this.phone = phone; }

    public String getPincode()                 { return pincode; }
    public void setPincode(String pincode)     { this.pincode = pincode; }

    public BigDecimal getDailyRate()           { return dailyRate; }
    public void setDailyRate(BigDecimal rate)  { this.dailyRate = rate; }

    public Boolean getAvailable()              { return available; }
    public void setAvailable(Boolean available){ this.available = available; }

    public LocalDateTime getCreatedAt()        { return createdAt; }

    public Contractor getContractor()              { return contractor; }
    public void setContractor(Contractor contractor){ this.contractor = contractor; }
    public List<WorkerSkill> getWorkerSkills()              { return workerSkills; }
    public void setWorkerSkills(List<WorkerSkill> skills)   { this.workerSkills = skills; }


    public static Builder builder()            { return new Builder(); }

    public static class Builder {
        private String name, phone, pincode;
        private BigDecimal dailyRate;
        private Boolean available = true;
        private Contractor contractor;


        public Builder name(String name)           { this.name = name; return this; }
        public Builder phone(String phone)         { this.phone = phone; return this; }
        public Builder pincode(String pincode)     { this.pincode = pincode; return this; }
        public Builder dailyRate(BigDecimal rate)  { this.dailyRate = rate; return this; }
        public Builder available(Boolean a)        { this.available = a; return this; }
        public Builder contractor(Contractor c)    { this.contractor = c; return this; }


        public Worker build() {
            Worker w = new Worker();
            w.name = this.name;
            w.phone = this.phone;
            w.pincode = this.pincode;
            w.dailyRate = this.dailyRate;
            w.available = this.available;
            w.contractor = this.contractor;
            return w;
        }
    }
}