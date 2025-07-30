package com.example.exam.prep.model;
import lombok.Data;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "options")
public class Option extends BaseEntity {
    @Column(nullable = false, columnDefinition = "NVARCHAR(1000)")
    private String text; // The text of the option

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question; // Reference back to the associated question

    @Column(nullable = false)
    private boolean correct; // Flag to indicate if this option is the correct answer

    @Column(name = "display_order")
    private int order;
}
