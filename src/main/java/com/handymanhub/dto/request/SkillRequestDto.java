package com.handymanhub.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class SkillRequestDto {
    @NotBlank(message = "Skill name is required")
    @Size(max=100,message = "Skill name cannot exceed 100 characters")
    private String name;
    @NotBlank(message="Category is required")
    @Size(max=100,message = "Category is required")
    @Size(max = 100, message = "Category cannot exceed 100 characters")
    private String category;

    private String description;

    public String getName()                  { return name; }
    public void setName(String name)         { this.name = name; }

    public String getCategory()              { return category; }
    public void setCategory(String cat)      { this.category = cat; }

    public String getDescription()           { return description; }
    public void setDescription(String desc)  { this.description = desc; }


}
