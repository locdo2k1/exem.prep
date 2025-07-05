package com.example.exam.prep.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "test_categories")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TestCategory extends BaseEntity {
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    // Constructors
    public TestCategory() {}

    public TestCategory(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
