package com.example.exam.prep.service;

import com.example.exam.prep.model.Option;
import com.example.exam.prep.repository.OptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OptionService {

    @Autowired
    private OptionRepository optionRepository;

    public List<Option> getAllOptions() {
        return optionRepository.findAll();
    }

    public Option createOption(Option option, String insertedBy) {
        option.setInsertedBy(insertedBy);
        return optionRepository.save(option);
    }

    // Additional methods for updating, deleting, etc. can be added here
}
