package ru.practicum.explore.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explore.dto.category.CategoryDto;
import ru.practicum.explore.model.Category;

@UtilityClass
public class CategoryMapper {

    public static CategoryDto categoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
