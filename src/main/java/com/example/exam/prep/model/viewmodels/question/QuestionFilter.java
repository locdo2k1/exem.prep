package com.example.exam.prep.model.viewmodels.question;

import com.example.exam.prep.core.filter.Filter;
import com.example.exam.prep.model.Question;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class QuestionFilter implements Filter<Question> {
    private UUID questionTypeId;
    private UUID categoryId;
    private Integer minScore;
    private Integer maxScore;
    private Integer clipNumber;
    private String prompt;

    @Override
    public Specification<Question> toSpecification() {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (questionTypeId != null) {
                predicates.add(cb.equal(root.get("questionType").get("id"), questionTypeId));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (clipNumber != null) {
                predicates.add(cb.equal(root.get("clipNumber"), clipNumber));
            }
            if (minScore != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("score"), minScore));
            }
            if (maxScore != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("score"), maxScore));
            }
            if (prompt != null && !prompt.isEmpty()) {
                predicates.add(cb.like(
                    cb.lower(root.get("prompt")),
                    "%" + prompt.toLowerCase() + "%"
                ));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
