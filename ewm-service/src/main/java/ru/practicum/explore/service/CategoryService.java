package ru.practicum.explore.service;

import ru.practicum.explore.dto.category.CategoryDto;
import ru.practicum.explore.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto dto);
    void deleteCategory(Long catId);
    CategoryDto updateCategory(Long catId, CategoryDto dto);
    List<CategoryDto> getCategories(int from, int size);
    CategoryDto getCategoryById(Long catId);
}
