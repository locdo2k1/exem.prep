package com.example.exam.prep.controller;

import com.example.exam.prep.constant.response.PartResponseMessage;
import com.example.exam.prep.model.Part;
import com.example.exam.prep.service.PartService;
import com.example.exam.prep.viewmodel.PartViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/parts")
public class PartController {

    private final PartService partService;

    @Autowired
    public PartController(PartService partService) {
        this.partService = partService;
    }

    @GetMapping
    public ResponseEntity<Page<PartViewModel>> getAllParts(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<PartViewModel> parts = partService.getAllPartViewModels(search, pageable);
        return new ResponseEntity<>(parts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartViewModel> getPartById(@PathVariable UUID id) {
        PartViewModel part = partService.getPartViewModelById(id);
        return new ResponseEntity<>(part, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Part> createPart(@RequestBody Part part) {
        Part createdPart = partService.create(part);
        return new ResponseEntity<>(createdPart, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Part> updatePart(
            @PathVariable UUID id,
            @RequestBody Part partDetails) {
        Part updatedPart = partService.update(id, partDetails);
        return new ResponseEntity<>(updatedPart, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePart(@PathVariable UUID id) {
        partService.delete(id);
        return new ResponseEntity<>(
                PartResponseMessage.PART_DELETED.getMessage(),
                HttpStatus.NO_CONTENT
        );
    }
}
