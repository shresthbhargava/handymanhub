package com.handymanhub.model;
import jakarta.persistence.*;


@Entity
@Table(name="skills")
public class Skill {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false,unique = true,length=100)
    private String name;
    @Column(length=100)
    private String category;
    @Column(columnDefinition="Text")
    private String description;

    public Skill() {}

    public Long getId()                     {return id;}
    public void setId(Long id)              {this.id= id; }

    public String getName()                 {return name;}
    public void setName(String name)        {this.name=name; }

    public String getCategory()             {return category;}
    public void setCategory(String category){this.category=category; }

    public String getDescription()           {return description ;}
    public void setDescription(String desc)  {this.description=desc; }

    public static  Builder builder() { return new Builder();   }
    public static class Builder {
        private String name;
        private String category;
        private String description;

        public Builder name(String name)           { this.name = name; return this; }
        public Builder category(String category)   { this.category = category; return this; }
        public Builder description(String desc)    { this.description = desc; return this; }


        public Skill build() {
            Skill s = new Skill();
            s.name = this.name;
            s.category = this.category;
            s.description = this.description;
            return s;
        }

    }




}
