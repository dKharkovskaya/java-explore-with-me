package ru.practicum.explore.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explore.dto.category.CategoryDto;
import ru.practicum.explore.dto.category.NewCategoryDto;
import ru.practicum.explore.error.exception.ConflictException;
import ru.practicum.explore.error.exception.NotFoundException;
import ru.practicum.explore.mapper.CategoryMapper;
import ru.practicum.explore.model.Category;
import ru.practicum.explore.repository.CategoryRepository;
import ru.practicum.explore.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new ConflictException("Категория с именем '" + newCategoryDto.getName() + "' уже существует");
        }
        Category category = categoryRepository.save(Category.builder()
                .name(newCategoryDto.getName())
                .build());
        return CategoryMapper.categoryDto(category);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(NotFoundException::new);
        if (categoryRepository.existsByName(categoryDto.getName()) && (!category.getName().equals(categoryDto.getName()))) {
            throw new ConflictException("Категория с именем '" + categoryDto.getName() + "' уже существует");
        }
        category.setName(categoryDto.getName());
        categoryDto.setId(category.getId());
        return categoryDto;
    }

    @Transactional
    @Override
    public void deleteCategory(Long catId) {
        if (eventRepository.existsByCategoryId(catId)) {
            throw new ConflictException("Нельзя удалить категорию" + catId + " потому что она привязана " +
                    "к событию");
        }
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
        Pageable pageable = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::categoryDto)
                .toList();
    }
}
