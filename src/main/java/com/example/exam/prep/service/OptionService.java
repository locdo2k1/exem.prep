package com.example.exam.prep.service;

import com.example.exam.prep.model.Option;
import com.example.exam.prep.repository.IOptionRepository;
import com.example.exam.prep.service.base.BaseService;
import org.springframework.stereotype.Service;

@Service
public class OptionService extends BaseService<Option> {
    private final IOptionRepository optionRepository;

    public OptionService(IOptionRepository optionRepository) {
        super(optionRepository);
        this.optionRepository = optionRepository;
    }
}

