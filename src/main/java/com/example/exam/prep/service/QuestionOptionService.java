package com.example.exam.prep.service;

import com.example.exam.prep.model.FillBlankAnswer;
import com.example.exam.prep.model.QuestionOption;
import com.example.exam.prep.repository.IFillBlankAnswerRepository;
import com.example.exam.prep.repository.IQuestionOptionRepository;
import com.example.exam.prep.service.base.BaseService;
import org.springframework.stereotype.Service;

@Service
public class QuestionOptionService extends BaseService<QuestionOption> {
    private final IQuestionOptionRepository questionOptionRepository;

    public QuestionOptionService(IQuestionOptionRepository questionOptionRepository) {
        super(questionOptionRepository);
        this.questionOptionRepository = questionOptionRepository;
    }

    @Service
    public static class FillBlankAnswerService extends BaseService<FillBlankAnswer> {
        private final IFillBlankAnswerRepository fillBlankAnswerRepository;

        public FillBlankAnswerService(IFillBlankAnswerRepository fillBlankAnswerRepository) {
            super(fillBlankAnswerRepository);
            this.fillBlankAnswerRepository = fillBlankAnswerRepository;
        }
    }
}

