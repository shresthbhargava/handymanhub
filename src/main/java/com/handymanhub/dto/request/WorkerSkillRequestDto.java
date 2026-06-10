package com.handymanhub.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class WorkerSkillRequestDto {

    @Min(value = 0, message = "Experience years cannot be negative")
    @Max(value = 50, message = "Experience years seems too high")
    private Integer experienceYears;

    public Integer getExperienceYears()          { return experienceYears; }
    public void setExperienceYears(Integer years){ this.experienceYears = years; }
}