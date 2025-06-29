package com.example.exam.prep.model.viewmodels.questionset;

import com.example.exam.prep.model.QuestionSetItem;
import com.example.exam.prep.model.Question;
import com.example.exam.prep.model.viewmodels.question.QuestionViewModel;
import com.example.exam.prep.model.viewmodels.question.QuestionCategoryViewModel;
import com.example.exam.prep.model.viewmodels.question.QuestionTypeViewModel;
import com.example.exam.prep.model.viewmodels.option.OptionViewModel;
import com.example.exam.prep.model.viewmodels.file.FileInfoViewModel;
import com.example.exam.prep.model.FillBlankAnswer;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * View model for QuestionSet responses.
 * Provides a clean API response model that includes only necessary fields.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionSetVM {
    private UUID id;
    private String title;
    private String description;
    private String imageUrl;
    private Integer order;
    private List<QuestionViewModel> questions;
    private int totalQuestions;
    private int totalScore;
    
    /**
     * Converts a Question entity to a QuestionViewModel
     */
    private static QuestionViewModel toQuestionViewModel(Question question, Integer customScore) {
        if (question == null) {
            return null;
        }
        
        QuestionViewModel viewModel = new QuestionViewModel();
        viewModel.setId(question.getId());
        viewModel.setPrompt(question.getPrompt());
        viewModel.setScore(customScore != null ? customScore : question.getScore());
        
        // Set question category if available
        if (question.getCategory() != null) {
            QuestionCategoryViewModel categoryVM = new QuestionCategoryViewModel();
            categoryVM.setId(question.getCategory().getId());
            categoryVM.setCode(question.getCategory().getCode());
            categoryVM.setName(question.getCategory().getName());
            categoryVM.setSkill(question.getCategory().getSkill());
            viewModel.setQuestionCategory(categoryVM);
        }
        
        // Set question type if available
        if (question.getQuestionType() != null) {
            QuestionTypeViewModel typeVM = new QuestionTypeViewModel();
            typeVM.setId(question.getQuestionType().getId());
            typeVM.setName(question.getQuestionType().getName());
            viewModel.setQuestionType(typeVM);
        }
        
        // Map options if available
        if (question.getOptions() != null) {
            List<OptionViewModel> optionViewModels = question.getOptions().stream()
                    .map(option -> {
                        OptionViewModel optionVM = new OptionViewModel();
                        optionVM.setId(option.getId());
                        optionVM.setText(option.getText());
                        optionVM.setCorrect(option.isCorrect());
                        return optionVM;
                    })
                    .collect(Collectors.toList());
            viewModel.setOptions(optionViewModels);
        }
        
        // Map fill blank answers if available
        if (question.getFillBlankAnswers() != null && !question.getFillBlankAnswers().isEmpty()) {
            List<String> answers = question.getFillBlankAnswers().stream()
                    .map(FillBlankAnswer::getAnswerText)
                    .collect(Collectors.toList());
            viewModel.setQuestionAnswers(answers);
        } else {
            viewModel.setQuestionAnswers(new ArrayList<>());
        }
        
        // Map question audios if available
        if (question.getFileInfos() != null) {
            List<FileInfoViewModel> audioViewModels = question.getFileInfos().stream()
                    .filter(fileInfo -> fileInfo.getFileType() != null && 
                            fileInfo.getFileType().startsWith("audio/"))
                    .map(fileInfo -> {
                        FileInfoViewModel fileInfoVM = new FileInfoViewModel();
                        fileInfoVM.setId(fileInfo.getId());
                        fileInfoVM.setFileName(fileInfo.getFileName());
                        fileInfoVM.setFileType(fileInfo.getFileType());
                        fileInfoVM.setFileUrl(fileInfo.getUrl()); // Using getUrl() instead of getFileUrl()
                        return fileInfoVM;
                    })
                    .collect(Collectors.toList());
            viewModel.setQuestionAudios(audioViewModels);
        }
        
        return viewModel;
    }
    

    /**
     * Static factory method to create a QuestionSetVM from a QuestionSet entity.
     * @param questionSet The QuestionSet entity to convert
     * @return A new QuestionSetVM instance
     */
    public static QuestionSetVM fromEntity(com.example.exam.prep.model.QuestionSet questionSet) {
        if (questionSet == null) {
            return null;
        }
        
        // Convert QuestionSetItems to QuestionViewModels
        List<QuestionViewModel> questions = questionSet.getQuestionSetItems().stream()
                .filter(QuestionSetItem::getIsActive)
                .map(item -> toQuestionViewModel(item.getQuestion(), item.getCustomScore()))
                .collect(Collectors.toList());
        
        // Calculate total score
        int totalScore = questions.stream()
                .mapToInt(QuestionViewModel::getScore)
                .sum();
        
        return QuestionSetVM.builder()
                .id(questionSet.getId())
                .title(questionSet.getTitle())
                .description(questionSet.getDescription())
                .imageUrl(questionSet.getImageUrl())
                .order(questionSet.getOrder())
                .questions(questions)
                .totalQuestions(questions.size())
                .totalScore(totalScore)
                .build();
    }
}
