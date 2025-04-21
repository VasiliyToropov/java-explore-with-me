package ru.practicum.admin.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.admin.model.category.Category;
import ru.practicum.admin.model.category.NewCategoryDto;
import ru.practicum.admin.repository.CategoryRepository;
import ru.practicum.exceptions.exception.NotFoundException;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category addNewCategory(NewCategoryDto newCategoryDto) {

        Category category = new Category();
        category.setName(newCategoryDto.getName());

        categoryRepository.save(category);

        log.info("Добавлена новая категория: {}", category.getName());

        return category;
    }

    @Override
    public void deleteCategory(Long catId) {
        Optional<Category> foundedCategoryDto = categoryRepository.findById(catId);

        Category category = foundedCategoryDto.orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));

        log.info("Удалена категория с id: {}", catId);

        categoryRepository.delete(category);

    }

    @Override
    public Category updateCategory(NewCategoryDto newCategoryDto, Long catId) {
        Optional<Category> foundedCategoryDto = categoryRepository.findById(catId);

        Category category = foundedCategoryDto.orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));

        category.setName(newCategoryDto.getName());

        log.info("Обновлена категория с id: {}", catId);

        categoryRepository.save(category);

        return category;
    }

    @Override
    public Category getCategoryById(Long catId) {
        Optional<Category> foundedCategory = categoryRepository.findById(catId);

        log.info("Получили категорию с id: {}", catId);

        return foundedCategory.orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
    }
}
