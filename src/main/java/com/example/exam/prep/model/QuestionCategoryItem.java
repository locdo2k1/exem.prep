package com.example.exam.prep.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the many-to-many relationship between Question and QuestionCategory.
 * This mapping table allows a question to belong to multiple categories
 * and a category to contain multiple questions.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "question_category_items")
public class QuestionCategoryItem extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private Question question;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private QuestionCategory category;
    
    @Column(name = "display_order")
    private Integer order;
    
    public QuestionCategoryItem(Question question, QuestionCategory category, Integer order) {
        this.question = question;
        this.category = category;
        this.order = order;
    }
}
