package com.handymanhub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "worker_skills")
public class WorkerSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "experience_years")
    private Integer experienceYears;

    public WorkerSkill() {}

    public Long getId()                           { return id; }
    public void setId(Long id)                    { this.id = id; }

    public Worker getWorker()                     { return worker; }
    public void setWorker(Worker worker)          { this.worker = worker; }

    public Skill getSkill()                       { return skill; }
    public void setSkill(Skill skill)             { this.skill = skill; }

    public Integer getExperienceYears()           { return experienceYears; }
    public void setExperienceYears(Integer years) { this.experienceYears = years; }

    public static Builder builder()               { return new Builder(); }

    public static class Builder {
        private Worker worker;
        private Skill skill;
        private Integer experienceYears;

        public Builder worker(Worker w)          { this.worker = w; return this; }
        public Builder skill(Skill s)            { this.skill = s; return this; }
        public Builder experienceYears(Integer y){ this.experienceYears = y; return this; }

        public WorkerSkill build() {
            WorkerSkill ws = new WorkerSkill();
            ws.worker = this.worker;
            ws.skill = this.skill;
            ws.experienceYears = this.experienceYears;
            return ws;
        }
    }
}