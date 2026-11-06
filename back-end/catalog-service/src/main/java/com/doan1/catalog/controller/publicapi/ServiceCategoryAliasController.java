package com.doan1.catalog.controller.publicapi;

import com.doan1.catalog.dto.response.CategoryResponse;
import com.doan1.catalog.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ServiceCategoryAliasController {
    private final CategoryService categories;

    public ServiceCategoryAliasController(CategoryService categories) {
        this.categories = categories;
    }

    @GetMapping("/api/services/categories")
    public List<CategoryResponse> categories() {
        return categories.list();
    }
}
