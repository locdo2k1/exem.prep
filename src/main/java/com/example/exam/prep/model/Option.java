package com.example.exam.prep.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents an option for a question in the exam system.
 */
@Entity
@Table(name = "options")
@Data
@EqualsAndHashCode(callSuper = true, exclude = "questionResponseOptions")
@SQLRestriction("is_deleted = 0")
public class Option extends BaseEntity {
    @Column(nullable = false, columnDefinition = "NVARCHAR(1000)")
    private String text; // The text of the option

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private Question question; // Reference back to the associated question

    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore  // Prevent infinite recursion in JSON serialization
    private Set<QuestionResponseOption> questionResponseOptions = new HashSet<>();

    @Column(nullable = false)
    private boolean correct; // Flag to indicate if this option is the correct answer

    @Column(name = "display_order")
    private Integer displayOrder;  // Changed from 'order' to 'displayOrder' to avoid SQL reserved word conflict
    
    /**
     * Backward compatibility getter for displayOrder.
     * @return the display order of this option
     */
    public Integer getOrder() {
        return displayOrder;
    }
    
    /**
     * Sets the display order of this option.
     * @param order the display order to set
     */
    public void setOrder(Integer order) {
        this.displayOrder = order;
    }
}
