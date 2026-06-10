package com.handymanhub.dto.response;

public class WorkerSkillResponseDto {

    private Long id;
    private Long workerId;
    private String workerName;
    private String workerPincode;
    private Long skillId;
    private String skillName;
    private String skillCategory;
    private Integer experienceYears;
    private Boolean workerAvailable;

    private WorkerSkillResponseDto() {}

    public Long getId()                { return id; }
    public Long getWorkerId()          { return workerId; }
    public String getWorkerName()      { return workerName; }
    public String getWorkerPincode()   { return workerPincode; }
    public Long getSkillId()           { return skillId; }
    public String getSkillName()       { return skillName; }
    public String getSkillCategory()   { return skillCategory; }
    public Integer getExperienceYears(){ return experienceYears; }
    public Boolean getWorkerAvailable(){ return workerAvailable; }

    public static Builder builder()    { return new Builder(); }

    public static class Builder {
        private final WorkerSkillResponseDto dto = new WorkerSkillResponseDto();

        public Builder id(Long v)                  { dto.id = v; return this; }
        public Builder workerId(Long v)            { dto.workerId = v; return this; }
        public Builder workerName(String v)        { dto.workerName = v; return this; }
        public Builder workerPincode(String v)     { dto.workerPincode = v; return this; }
        public Builder skillId(Long v)             { dto.skillId = v; return this; }
        public Builder skillName(String v)         { dto.skillName = v; return this; }
        public Builder skillCategory(String v)     { dto.skillCategory = v; return this; }
        public Builder experienceYears(Integer v)  { dto.experienceYears = v; return this; }
        public Builder workerAvailable(Boolean v)  { dto.workerAvailable = v; return this; }

        public WorkerSkillResponseDto build()      { return dto; }
    }
}