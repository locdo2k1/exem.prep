package com.example.exam.prep.service;

import com.example.exam.prep.model.Part;
import com.example.exam.prep.repository.IPartRepository;
import com.example.exam.prep.service.base.BaseService;
import org.springframework.stereotype.Service;

@Service
public class PartService extends BaseService<Part> {
    private final IPartRepository partRepository;

    public PartService(IPartRepository partRepository) {
        super(partRepository);
        this.partRepository = partRepository;
    }
}

