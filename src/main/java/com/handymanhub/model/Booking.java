package com.handymanhub.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    public enum Status {
        PENDING,
        CONFIRMED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "worker_id")
    private Worker worker;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contractor_id")
    private Contractor contractor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.PENDING;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Booking() {}

    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }

    public Customer getCustomer()              { return customer; }
    public void setCustomer(Customer c)        { this.customer = c; }

    public Worker getWorker()                  { return worker; }
    public void setWorker(Worker w)            { this.worker = w; }

    public Contractor getContractor()          { return contractor; }
    public void setContractor(Contractor c)    { this.contractor = c; }

    public Skill getSkill()                    { return skill; }
    public void setSkill(Skill s)              { this.skill = s; }

    public LocalDate getScheduledDate()        { return scheduledDate; }
    public void setScheduledDate(LocalDate d)  { this.scheduledDate = d; }

    public Integer getDurationDays()           { return durationDays; }
    public void setDurationDays(Integer d)     { this.durationDays = d; }

    public Status getStatus()                  { return status; }
    public void setStatus(Status status)       { this.status = status; }

    public String getAddress()                 { return address; }
    public void setAddress(String address)     { this.address = address; }

    public String getNotes()                   { return notes; }
    public void setNotes(String notes)         { this.notes = notes; }

    public LocalDateTime getCreatedAt()        { return createdAt; }

    public static Builder builder()            { return new Builder(); }

    public static class Builder {
        private Customer customer;
        private Worker worker;
        private Contractor contractor;
        private Skill skill;
        private LocalDate scheduledDate;
        private Integer durationDays = 1;
        private Status status = Status.PENDING;
        private String address, notes;

        public Builder customer(Customer v)        { this.customer = v; return this; }
        public Builder worker(Worker v)            { this.worker = v; return this; }
        public Builder contractor(Contractor v)    { this.contractor = v; return this; }
        public Builder skill(Skill v)              { this.skill = v; return this; }
        public Builder scheduledDate(LocalDate v)  { this.scheduledDate = v; return this; }
        public Builder durationDays(Integer v)     { this.durationDays = v; return this; }
        public Builder status(Status v)            { this.status = v; return this; }
        public Builder address(String v)           { this.address = v; return this; }
        public Builder notes(String v)             { this.notes = v; return this; }

        public Booking build() {
            Booking b = new Booking();
            b.customer = this.customer;
            b.worker = this.worker;
            b.contractor = this.contractor;
            b.skill = this.skill;
            b.scheduledDate = this.scheduledDate;
            b.durationDays = this.durationDays;
            b.status = this.status;
            b.address = this.address;
            b.notes = this.notes;
            return b;
        }
    }
}