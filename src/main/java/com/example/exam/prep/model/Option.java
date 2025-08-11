package com.example.exam.prep.model;
import lombok.Data;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, exclude = "questionResponseOptions")
@Table(name = "options")
public class Option extends BaseEntity {
    @Column(nullable = false, columnDefinition = "NVARCHAR(1000)")
    private String text; // The text of the option

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question; // Reference back to the associated question

    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuestionResponseOption> questionResponseOptions = new HashSet<>();

    @Column(nullable = false)
    private boolean correct; // Flag to indicate if this option is the correct answer

    @Column(name = "display_order")
    private int order;
}
