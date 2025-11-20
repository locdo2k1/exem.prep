package com.example.exam.prep.vm;

import com.example.exam.prep.model.Part;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartViewModel {
    private UUID id;
    private String name;
    private String description;

    public static PartViewModel fromModel(Part part) {
        if (part == null) {
            return null;
        }
        PartViewModel viewModel = new PartViewModel();
        viewModel.setId(part.getId());
        viewModel.setName(part.getName());
        viewModel.setDescription(part.getDescription());
        return viewModel;
    }
}
