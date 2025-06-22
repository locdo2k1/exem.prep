package com.example.exam.prep.service;

import com.example.exam.prep.constant.QuestionTypeConstant;
import com.example.exam.prep.model.*;
import com.example.exam.prep.model.viewmodels.file.FileInfoViewModel;
import com.example.exam.prep.model.viewmodels.option.CreateQuestionOptionViewModel;
import com.example.exam.prep.model.viewmodels.option.OptionViewModel;
import com.example.exam.prep.model.viewmodels.question.*;
import com.example.exam.prep.repository.IQuestionRepository;
import com.example.exam.prep.repository.IQuestionTypeRepository;
import com.example.exam.prep.repository.IQuestionCategoryRepository;
import com.example.exam.prep.constant.file.FileConstant;
import com.example.exam.prep.unitofwork.IUnitOfWork;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

    public Page<QuestionViewModel> findAll(QuestionFilter filter, Pageable pageable) {
        Page<Question> questions = questionRepository.findAll(filter, pageable);
        return questions.map(question -> {
            QuestionViewModel viewModel = new QuestionViewModel();
            viewModel.setId(question.getId());
            viewModel.setPrompt(question.getPrompt());

            // Map category to view model
            if (question.getCategory() != null) {
                QuestionCategory category = question.getCategory();
                QuestionCategoryViewModel categoryVM = new QuestionCategoryViewModel();
                categoryVM.setId(category.getId());
                categoryVM.setCode(category.getCode());
                categoryVM.setName(category.getName());
                categoryVM.setSkill(category.getSkill());
                viewModel.setQuestionCategory(categoryVM);
            }

            // Map question type to view model
            if (question.getQuestionType() != null) {
                QuestionType questionType = question.getQuestionType();
                QuestionTypeViewModel typeVM = new QuestionTypeViewModel();
                typeVM.setId(questionType.getId());
                typeVM.setName(questionType.getName());
                typeVM.setDescription(questionType.getName());
                viewModel.setQuestionType(typeVM);
            }
            viewModel.setScore(question.getScore() != null ? question.getScore() : 0);
            viewModel.setQuestionAnswers(question.getFillBlankAnswers().stream()
                    .map(FillBlankAnswer::getAnswerText)
                    .collect(Collectors.toList()));
            // Map options to view models
            viewModel.setOptions(question.getOptions().stream()
                    .map(option -> {
                        OptionViewModel optionVM = new OptionViewModel();
                        optionVM.setId(option.getId());
                        optionVM.setText(option.getText());
                        optionVM.setCorrect(option.isCorrect());
                        return optionVM;
                    })
                    .collect(Collectors.toList()));
            // Map file infos to view models
            viewModel.setQuestionAudios(question.getFileInfos().stream()
                    .map(fileInfo -> {
                        FileInfoViewModel fileInfoVM = new FileInfoViewModel();
                        fileInfoVM.setId(fileInfo.getId());
                        fileInfoVM.setFileName(fileInfo.getFileName());
                        fileInfoVM.setFileUrl(fileInfo.getUrl());
                        fileInfoVM.setFileType(fileInfo.getFileType());
                        fileInfoVM.setFileSize(fileInfo.getFileSize());
                        return fileInfoVM;
                    })
                    .collect(Collectors.toList()));
            return viewModel;
        });
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
        unitOfWork.getQuestionRepository().save(question);

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
    public Question updateQuestion(UUID id, UpdateQuestionViewModel updateDto) {
        return questionRepository.findById(id)
                .map(existingQuestion -> {
                    // Update only the fields that should be updated
                    QuestionType questionType = null;
                    if (updateDto.getQuestionTypeId() != null) {
                        questionType = questionTypeRepository.findById(updateDto.getQuestionTypeId())
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

                    // Handle file deletions
                    ObjectMapper objectMapper = new ObjectMapper();
                    if (updateDto.getDeletedAudiosIds() != null && !updateDto.getDeletedAudiosIds().isEmpty()) {
                        try {
                            List<UUID> deletedIds = objectMapper.readValue(
                                    updateDto.getDeletedAudiosIds(),
                                    new TypeReference<List<UUID>>() {
                                    }
                            );

                            existingQuestion.getFileInfos().removeIf(fileInfo -> deletedIds.contains(fileInfo.getId()));
                            deletedIds.forEach(fileId -> unitOfWork.getFileInfoRepository().deleteById(fileId));
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to parse or delete files: " + e.getMessage(), e);
                        }
                    }

                    //Update prompt and score
                    existingQuestion.setPrompt(updateDto.getPrompt());
                    existingQuestion.setScore(updateDto.getScore());

                    // Handle new file uploads
                    if (updateDto.getAudios() != null && !updateDto.getAudios().isEmpty()) {
                        try {
                            String filePath = FileConstant.QUESTION_FILES.getStringValue();
                            List<FileInfo> uploadedFiles = fileStorageService.uploadFiles(updateDto.getAudios(), filePath);

                            // Associate new files with the question
                            for (FileInfo fileInfo : uploadedFiles) {
                                fileInfo.setQuestion(existingQuestion);
                                unitOfWork.getFileInfoRepository().save(fileInfo);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to upload audio files", e);
                        }
                    }

                    // Handle options if provided
                    String multipleChoice = QuestionTypeConstant.MULTIPLE_CHOICE.toString();
                    if (updateDto.getOptions() != null && !updateDto.getOptions().isEmpty() && questionType != null && questionType.getName().equals(multipleChoice)) {
                        try {
                            // Clear existing options
                            existingQuestion.getOptions().forEach(option -> unitOfWork.getOptionRepository().delete(option));
                            existingQuestion.getFillBlankAnswers().forEach(blankAnswer -> unitOfWork.getFillBlankAnswerRepository().delete(blankAnswer));
                            existingQuestion.getOptions().clear();
                            existingQuestion.getFillBlankAnswers().clear();

                            // Parse and add new options
                            CreateQuestionOptionViewModel[] options = objectMapper.readValue(
                                    updateDto.getOptions(),
                                    CreateQuestionOptionViewModel[].class
                            );

                            for (CreateQuestionOptionViewModel optionDto : options) {
                                Option option = new Option();
                                option.setText(optionDto.getText());
                                option.setCorrect(optionDto.isCorrect());
                                option.setQuestion(existingQuestion);
                                unitOfWork.getOptionRepository().save(option);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to parse options: " + e.getMessage(), e);
                        }
                    }

                    // Handle blank answers if provided
                    String fillInBlank = QuestionTypeConstant.FILL_IN_THE_BLANK.getDisplayName(); // "Fill in the Blank"

                    if (updateDto.getBlankAnswers() != null && !updateDto.getBlankAnswers().isEmpty() && questionType != null && questionType.getName().equals(fillInBlank)) {
                        try {
                            // Clear existing blank answers
                            existingQuestion.getFillBlankAnswers().forEach(blankAnswer -> unitOfWork.getFillBlankAnswerRepository().delete(blankAnswer));
                            existingQuestion.getOptions().forEach(option -> unitOfWork.getOptionRepository().delete(option));
                            existingQuestion.getOptions().clear();
                            existingQuestion.getFillBlankAnswers().clear();

                            // Parse and add new blank answers
                            List<String> blankAnswers = objectMapper.readValue(
                                    updateDto.getBlankAnswers(),
                                    new TypeReference<List<String>>() {
                                    }
                            );

                            for (String answerText : blankAnswers) {
                                FillBlankAnswer blankAnswer = new FillBlankAnswer();
                                blankAnswer.setAnswerText(answerText);
                                blankAnswer.setQuestion(existingQuestion);
                                unitOfWork.getFillBlankAnswerRepository().save(blankAnswer);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to parse blank answers: " + e.getMessage(), e);
                        }
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
                    viewModel.setId(question.getId());
                    viewModel.setPrompt(question.getPrompt());

                    // Map category to view model
                    if (question.getCategory() != null) {
                        QuestionCategory category = question.getCategory();
                        QuestionCategoryViewModel categoryVM = new QuestionCategoryViewModel();
                        categoryVM.setId(category.getId());
                        categoryVM.setCode(category.getCode());
                        categoryVM.setName(category.getName());
                        categoryVM.setSkill(category.getSkill());
                        viewModel.setQuestionCategory(categoryVM);
                    }

                    // Map question type to view model
                    if (question.getQuestionType() != null) {
                        QuestionType questionType = question.getQuestionType();
                        QuestionTypeViewModel typeVM = new QuestionTypeViewModel();
                        typeVM.setId(questionType.getId());
                        typeVM.setName(questionType.getName());
                        typeVM.setDescription(questionType.getName());
                        viewModel.setQuestionType(typeVM);
                    }
                    viewModel.setScore(question.getScore() != null ? question.getScore() : 0);
                    viewModel.setQuestionAnswers(question.getFillBlankAnswers().stream()
                            .map(FillBlankAnswer::getAnswerText)
                            .collect(Collectors.toList()));
                    // Map options to view models
                    viewModel.setOptions(question.getOptions().stream()
                            .map(option -> {
                                OptionViewModel optionVM = new OptionViewModel();
                                optionVM.setId(option.getId());
                                optionVM.setText(option.getText());
                                optionVM.setCorrect(option.isCorrect());
                                return optionVM;
                            })
                            .collect(Collectors.toList()));
                    // Map file infos to view models
            viewModel.setQuestionAudios(question.getFileInfos().stream()
                    .map(fileInfo -> {
                        FileInfoViewModel fileInfoVM = new FileInfoViewModel();
                        fileInfoVM.setId(fileInfo.getId());
                        fileInfoVM.setFileName(fileInfo.getFileName());
                        fileInfoVM.setFileUrl(fileInfo.getUrl());
                        fileInfoVM.setFileType(fileInfo.getFileType());
                        fileInfoVM.setFileSize(fileInfo.getFileSize());
                        return fileInfoVM;
                    })
                    .collect(Collectors.toList()));
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
