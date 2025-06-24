package com.example.exam.prep.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "question_set_files")
public class QuestionSetFile extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_set_id", nullable = false)
    private QuestionSet questionSet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_info_id", nullable = false)
    private FileInfo fileInfo;

    @Column(name = "is_primary")
    private Boolean isPrimary = false;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "inserted_at", nullable = false, updatable = false, insertable = false)
    @Override
    public Instant getInsertedAt() {
        return super.getInsertedAt();
    }

    // Constructors
    public QuestionSetFile() {}

    public QuestionSetFile(QuestionSet questionSet, FileInfo fileInfo) {
        this.questionSet = questionSet;
        this.fileInfo = fileInfo;
    }

    public QuestionSetFile(QuestionSet questionSet, FileInfo fileInfo, boolean isPrimary, Integer displayOrder) {
        this.questionSet = questionSet;
        this.fileInfo = fileInfo;
        this.isPrimary = isPrimary;
        this.displayOrder = displayOrder;
    }
}
