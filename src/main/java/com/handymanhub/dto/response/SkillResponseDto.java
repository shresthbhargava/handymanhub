package com.handymanhub.dto.response;

public class SkillResponseDto {

    private Long id;
    private String name;
    private String category;
    private String description;

    private SkillResponseDto() {}

    public Long getId()             { return id; }
    public String getName()         { return name; }
    public String getCategory()     { return category; }
    public String getDescription()  { return description; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final SkillResponseDto dto = new SkillResponseDto();

        public Builder id(Long v)            { dto.id = v; return this; }
        public Builder name(String v)        { dto.name = v; return this; }
        public Builder category(String v)    { dto.category = v; return this; }
        public Builder description(String v) { dto.description = v; return this; }

        public SkillResponseDto build()      { return dto; }
    }
}