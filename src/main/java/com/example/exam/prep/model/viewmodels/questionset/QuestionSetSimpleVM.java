package com.example.exam.prep.model.viewmodels.questionset;

import com.example.exam.prep.model.QuestionSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSetSimpleVM {
    private UUID id;
    private String title;
    
    public static QuestionSetSimpleVM fromEntity(QuestionSet questionSet) {
        if (questionSet == null) {
            return null;
        }
        return new QuestionSetSimpleVM(
            questionSet.getId(),
            questionSet.getTitle()
        );
    }
}
