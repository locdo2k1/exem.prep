package com.example.exam.prep.service;

import com.example.exam.prep.constant.response.PartResponseMessage;
import com.example.exam.prep.exception.ResourceNotFoundException;
import com.example.exam.prep.model.Part;
import com.example.exam.prep.model.TestPart;
import com.example.exam.prep.repository.IPartRepository;
import com.example.exam.prep.repository.ITestPartRepository;
import com.example.exam.prep.service.base.BaseService;
import com.example.exam.prep.viewmodel.PartViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PartService extends BaseService<Part> {
    private final IPartRepository partRepository;
    private final ITestPartRepository testPartRepository;

    @Autowired
    public PartService(IPartRepository partRepository, ITestPartRepository testPartRepository) {
        super(partRepository);
        this.partRepository = partRepository;
        this.testPartRepository = testPartRepository;
    }

    @Override
    public Optional<Part> findById(UUID id) {
        return partRepository.findById(id);
    }

    @Override
    public List<Part> findAll() {
        return partRepository.findAll();
    }

    public Optional<TestPart> getTestPartByPartIdAndTestId(UUID partId, UUID testId) {
        return testPartRepository.findByPartIdAndTestId(partId, testId);
    }

    public Part getById(UUID id) {
        return findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PartResponseMessage.PART_NOT_FOUND.getMessage() + id));
    }

    public Page<Part> findAll(String search, Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            return partRepository.findByNameContainingIgnoreCase(search, pageable);
        }
        return partRepository.findAll(pageable);
    }

    public Part create(Part part) {
        // Check if part with same name already exists
        partRepository.findByName(part.getName()).ifPresent(p -> {
            throw new IllegalStateException(PartResponseMessage.PART_ALREADY_EXISTS.getMessage());
        });
        return partRepository.save(part);
    }

    public Part update(UUID id, Part partDetails) {
        Part part = getById(id);
        part.setName(partDetails.getName());
        part.setDescription(partDetails.getDescription());
        return partRepository.save(part);
    }

    public void delete(UUID id) {
        Part part = getById(id);
        partRepository.delete(part);
    }

    public Page<PartViewModel> getAllPartViewModels(String search, Pageable pageable) {
        return findAll(search, pageable)
                .map(PartViewModel::fromModel);
    }

    public PartViewModel getPartViewModelById(UUID id) {
        return PartViewModel.fromModel(getById(id));
    }
    
    @Transactional(readOnly = true)
    public List<PartViewModel> getPartsByTestId(UUID testId) {
        List<TestPart> testParts = testPartRepository.findByTestIdWithParts(testId);
        return testParts.stream()
                .map(TestPart::getPart)
                .map(PartViewModel::fromModel)
                .collect(Collectors.toList());
    }
}

