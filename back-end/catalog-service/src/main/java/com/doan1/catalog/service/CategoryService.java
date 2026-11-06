package com.doan1.catalog.service;

import com.doan1.catalog.dto.response.CategoryResponse;
import com.doan1.catalog.entity.Category;
import com.doan1.catalog.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categories;

    public CategoryService(CategoryRepository categories) {
        this.categories = categories;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> list() {
        return categories.findAllByActiveTrueOrderByNameAsc().stream().map(this::response).toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse get(String id) {
        return categories.findById(id)
                .filter(Category::isActive)
                .map(this::response)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found."));
    }

    private CategoryResponse response(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }
}
