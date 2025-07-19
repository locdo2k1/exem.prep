package com.example.exam.prep.service;

import com.example.exam.prep.constant.response.PartResponseMessage;
import com.example.exam.prep.exception.ResourceNotFoundException;
import com.example.exam.prep.model.Part;
import com.example.exam.prep.repository.IPartRepository;
import com.example.exam.prep.service.base.BaseService;
import com.example.exam.prep.viewmodel.PartViewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PartService extends BaseService<Part> {
    private final IPartRepository partRepository;

    public PartService(IPartRepository partRepository) {
        super(partRepository);
        this.partRepository = partRepository;
    }

    @Override
    public Optional<Part> findById(UUID id) {
        return partRepository.findById(id);
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
}

