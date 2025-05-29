package com.example.exam.prep.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "parts")
public class Part extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @OneToMany(mappedBy = "part", cascade = CascadeType.ALL)
    private Set<TestPart> testParts;

    // Constructors
    public Part() {}

    public Part(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
