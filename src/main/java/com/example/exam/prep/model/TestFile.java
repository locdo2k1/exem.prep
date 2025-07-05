package com.example.exam.prep.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "test_files")
public class TestFile extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private FileInfo file;

    // Constructors
    protected TestFile() {}

    public TestFile(Test test, FileInfo file) {
        this.test = test;
        this.file = file;
    }
}
