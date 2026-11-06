package com.doan1.catalog.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

public final class PageableFactory {
    private static final Set<String> SORT_FIELDS = Set.of("name", "price", "createdAt", "updatedAt");

    private PageableFactory() {
    }

    public static Pageable create(int page, int size, String sort, String direction) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page must be non-negative and size must be 1-100.");
        }
        String sortField = sort == null ? "createdAt" : sort;
        if (!SORT_FIELDS.contains(sortField)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported sort field.");
        }
        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(direction == null ? "DESC" : direction);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sort direction must be ASC or DESC.");
        }
        return PageRequest.of(page, size, Sort.by(sortDirection, sortField));
    }
}
