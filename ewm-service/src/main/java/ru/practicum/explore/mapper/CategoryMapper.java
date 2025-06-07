package ru.practicum.explore.mapper;

import ru.practicum.explore.dto.category.CategoryDto;
import ru.practicum.explore.dto.category.NewCategoryDto;
import ru.practicum.explore.model.Category;

public class CategoryMapper {

    public static Category toCategory(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }

    public static CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
    public static void updateCategoryFromDto(Category category, CategoryDto categoryDto) {
        if (categoryDto.getName() != null && !categoryDto.getName().isBlank()) {
            category.setName(categoryDto.getName());
        }
    }
}
