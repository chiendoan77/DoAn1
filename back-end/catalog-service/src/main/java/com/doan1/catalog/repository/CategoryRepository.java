package com.doan1.catalog.repository;

import com.doan1.catalog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, String> {
    List<Category> findAllByActiveTrueOrderByNameAsc();
}
