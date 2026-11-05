package com.doan1.marketplace.dto;

import java.util.List;
import java.util.Set;

public record CategoryResponse(String id, String name, String description) {
    private static final List<CategoryResponse> CATEGORIES = List.of(
            new CategoryResponse("cleaning", "Dọn dẹp nhà", "Dọn dẹp định kỳ và tổng vệ sinh."),
            new CategoryResponse("repair", "Sửa chữa gia đình", "Kết nối thợ sửa chữa cho ngôi nhà."));

    public static List<CategoryResponse> all() {
        return CATEGORIES;
    }

    public static List<CategoryResponse> forIds(Set<String> categoryIds) {
        return CATEGORIES.stream().filter(category -> categoryIds.contains(category.id())).toList();
    }
}
