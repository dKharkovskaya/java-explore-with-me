package ru.practicum.explore.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explore.dto.category.CategoryDto;
import ru.practicum.explore.dto.category.NewCategoryDto;
import ru.practicum.explore.error.exception.NotFoundException;
import ru.practicum.explore.mapper.CategoryMapper;
import ru.practicum.explore.model.Category;
import ru.practicum.explore.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.save(Category.builder()
                .name(newCategoryDto.getName())
                .build());
        return CategoryMapper.categoryDto(category);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(NotFoundException::new);
        category.setName(categoryDto.getName());
        categoryDto.setId(category.getId());
        return categoryDto;
    }

    @Transactional
    @Override
    public void deleteCategory(Long catId) {
        categoryRepository.findById(catId).orElseThrow(NotFoundException::new);
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(NotFoundException::new);
        return CategoryMapper.categoryDto(category);
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        List<Category> allCategories = categoryRepository.findAll();
        return allCategories.stream()
                .skip(from)
                .limit(size)
                .map(CategoryMapper::categoryDto)
                .toList();
    }
}
