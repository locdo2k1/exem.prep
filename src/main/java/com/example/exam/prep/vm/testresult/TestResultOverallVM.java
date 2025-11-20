package com.example.exam.prep.vm.testresult;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.exam.prep.vm.PartViewModel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestResultOverallVM {
    private int totalQuestions;
    private int correctAnswers;
    private int incorrectAnswers;
    private int skippedQuestions;
    private double accuracyPercentage;
    private double score;
    private String completionTime; // format: HH:mm:ss
    private List<PartViewModel> parts;

    // Computed field for attempted questions
    public int getAttemptedQuestions() {
        return correctAnswers + incorrectAnswers;
    }
}
