package com.example.exam.prep.service;

import com.example.exam.prep.model.*;
import com.example.exam.prep.model.viewmodels.option.CreateQuestionOptionViewModel;
import com.example.exam.prep.model.viewmodels.question.CreateQuestionViewModel;
import com.example.exam.prep.model.viewmodels.question.QuestionViewModel;
import com.example.exam.prep.repository.IQuestionRepository;
import com.example.exam.prep.repository.IQuestionTypeRepository;
import com.example.exam.prep.repository.IQuestionCategoryRepository;
import com.example.exam.prep.constant.file.FileConstant;
import com.example.exam.prep.unitofwork.IUnitOfWork;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class QuestionService {
    private final IQuestionRepository questionRepository;
    private final IQuestionTypeRepository questionTypeRepository;
    private final IQuestionCategoryRepository questionCategoryRepository;
    private final IFileStorageService fileStorageService;
    private final IUnitOfWork unitOfWork;

    @Autowired
    public QuestionService(
            IQuestionRepository questionRepository,
            IQuestionTypeRepository questionTypeRepository,
            IQuestionCategoryRepository questionCategoryRepository,
            IFileStorageService fileStorageService,
            IUnitOfWork unitOfWork) {
        this.questionRepository = questionRepository;
        this.questionTypeRepository = questionTypeRepository;
        this.questionCategoryRepository = questionCategoryRepository;
        this.fileStorageService = fileStorageService;
        this.unitOfWork = unitOfWork;
    }

    public Page<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }

    @Transactional
    public Question createQuestion(CreateQuestionViewModel createDto) {
        // Validate required fields
        if (createDto.getQuestionTypeId() == null) {
            throw new IllegalArgumentException("Question type ID is required");
        }

        // Fetch related entities
        QuestionType questionType = questionTypeRepository.findById(createDto.getQuestionTypeId())
                .orElseThrow(() -> new EntityNotFoundException("QuestionType not found with id: " + createDto.getQuestionTypeId()));

        // Create and map the question
        Question question = new Question();
        question.setQuestionType(questionType);
        question.setPrompt(createDto.getPrompt());
        question.setScore(createDto.getScore());

        // Handle category if provided
        if (createDto.getCategoryId() != null) {
            QuestionCategory category = questionCategoryRepository.findById(createDto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("QuestionCategory not found with id: " + createDto.getCategoryId()));
            question.setCategory(category);
        }

        // Handle multiple audio file uploads if provided
        if (createDto.getAudios() != null && !createDto.getAudios().isEmpty()) {
            try {
                String filePath = FileConstant.QUESTION_FILES.getStringValue();
                List<FileInfo> audioFiles = fileStorageService.uploadFiles(createDto.getAudios(), filePath);
                audioFiles.forEach(fileInfo -> {
                    fileInfo.setQuestion(question);
                    unitOfWork.getFileInfoRepository().save(fileInfo);
                });
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload audio files", e);
            }
        }

        // Handle options if provided
        if (createDto.getOptions() != null && !createDto.getOptions().isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                CreateQuestionOptionViewModel[] options = mapper.readValue(createDto.getOptions(), CreateQuestionOptionViewModel[].class);
                for (CreateQuestionOptionViewModel option : options) {
                    Option createdOption = new Option();
                    createdOption.setText(option.getText());
                    createdOption.setCorrect(option.isCorrect());
                    createdOption.setQuestion(question);
                    unitOfWork.getOptionRepository().save(createdOption);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to parse question options", e);
            }
        }

        // Handle fillBlankAnswers if provided
        ObjectMapper objectMapper = new ObjectMapper();
        // Parse JSON string to List<String>
        List<String> blankAnswers = null;
        try {
            blankAnswers = objectMapper.readValue(createDto.getBlankAnswers(), new TypeReference<List<String>>() {
            });

            if (blankAnswers != null && !blankAnswers.isEmpty()) {
                blankAnswers.forEach(answerText -> {
                    FillBlankAnswer fillBlankAnswer = new FillBlankAnswer();
                    fillBlankAnswer.setQuestion(question);
                    fillBlankAnswer.setAnswerText(answerText);
                    unitOfWork.getFillBlankAnswerRepository().save(fillBlankAnswer);
                });
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Question savedQuestion = questionRepository.save(question);

        return savedQuestion;
    }

    @Transactional
    public Question updateQuestion(UUID id, CreateQuestionViewModel updateDto) {
        return questionRepository.findById(id)
                .map(existingQuestion -> {
                    // Update only the fields that should be updated
                    if (updateDto.getQuestionTypeId() != null) {
                        QuestionType questionType = questionTypeRepository.findById(updateDto.getQuestionTypeId())
                                .orElseThrow(() -> new EntityNotFoundException("QuestionType not found with id: " + updateDto.getQuestionTypeId()));
                        existingQuestion.setQuestionType(questionType);
                    }

                    if (updateDto.getCategoryId() != null) {
                        QuestionCategory category = questionCategoryRepository.findById(updateDto.getCategoryId())
                                .orElseThrow(() -> new EntityNotFoundException("QuestionCategory not found with id: " + updateDto.getCategoryId()));
                        existingQuestion.setCategory(category);
                    } else {
                        existingQuestion.setCategory(null);
                    }

                    // Map other fields from DTO to entity
                    existingQuestion.setId(id); // Ensure ID is preserved

                    return questionRepository.save(existingQuestion);
                })
                .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + id));
    }

    public Optional<QuestionViewModel> findById(UUID id) {
        return questionRepository.findWithDetailsById(id)
                .map(question -> {
                    QuestionViewModel viewModel = new QuestionViewModel();
                    viewModel.setPrompt(question.getPrompt());
                    viewModel.setQuestionCategory(question.getCategory());
                    viewModel.setQuestionType(question.getQuestionType());
                    viewModel.setScore(question.getScore());
                    viewModel.setQuestionAnswers(question.getFillBlankAnswers().stream()
                            .map(FillBlankAnswer::getAnswerText)
                            .toList());
                    viewModel.setOptions(question.getOptions().stream().toList());
                    viewModel.setQuestionAudios(question.getFileInfos().stream().toList());
                    return viewModel;
                });
    }

    public Question save(Question entity) {
        return questionRepository.save(entity);
    }

    public void deleteById(UUID id) {
        questionRepository.deleteById(id);
    }
}
