package com.example.exam.prep.core.filter;

import org.springframework.data.jpa.domain.Specification;

public interface Filter<T> {
    Specification<T> toSpecification();
}
