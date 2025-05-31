package com.example.exam.prep.unitofwork;

    import com.example.exam.prep.repository.*;

    public interface IUnitOfWork {
        IUserRepository getUserRepository();
        IFillBlankAnswerRepository getFillBlankAnswerRepository();
        IOptionRepository getOptionRepository();
        IPartRepository getPartRepository();
        IQuestionRepository getQuestionRepository();
        IQuestionCategoryRepository getQuestionCategoryRepository();
        IQuestionOptionRepository getQuestionOptionRepository();
        IQuestionResponseRepository getQuestionResponseRepository();
        IQuestionSetRepository getQuestionSetRepository();
        IQuestionTypeRepository getQuestionTypeRepository();
        ITestRepository getTestRepository();
        ITestAttemptRepository getTestAttemptRepository();
        ITestPartRepository getTestPartRepository();
    }