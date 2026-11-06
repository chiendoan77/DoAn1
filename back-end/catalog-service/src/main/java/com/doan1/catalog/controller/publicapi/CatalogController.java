package com.doan1.catalog.controller.publicapi;

import com.doan1.catalog.dto.response.CategoryResponse;
import com.doan1.catalog.dto.response.PackageResponse;
import com.doan1.catalog.dto.response.PageResponse;
import com.doan1.catalog.service.CategoryService;
import com.doan1.catalog.service.PackageWorkflowService;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalog")
@Validated
public class CatalogController {
    private final PackageWorkflowService workflow;
    private final CategoryService categories;

    public CatalogController(PackageWorkflowService workflow, CategoryService categories) {
        this.workflow = workflow;
        this.categories = categories;
    }

    @GetMapping("/packages")
    public PageResponse<PackageResponse> packages(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) @DecimalMin("1") @Digits(integer = 10, fraction = 0) BigDecimal minPrice,
            @RequestParam(required = false) @DecimalMin("1") @Digits(integer = 10, fraction = 0) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {
        return workflow.publicPackages(categoryId, q, minPrice, maxPrice, page, size, sort, direction);
    }

    @GetMapping("/packages/{id}")
    public PackageResponse packageById(@PathVariable UUID id) {
        return workflow.publicPackage(id);
    }

    @GetMapping("/categories")
    public List<CategoryResponse> categories() {
        return categories.list();
    }

    @GetMapping("/categories/{id}")
    public CategoryResponse category(@PathVariable String id) {
        return categories.get(id);
    }
}
