package com.example.exam.prep.model.viewmodels;

import com.example.exam.prep.model.BaseEntity;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriteriaQueryVM<T> {
    private CriteriaQuery<T> criteriaQuery;
    private Root<T> root;
}
