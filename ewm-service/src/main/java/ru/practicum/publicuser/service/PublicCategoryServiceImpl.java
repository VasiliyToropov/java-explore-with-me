package ru.practicum.publicuser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.admin.model.category.Category;
import ru.practicum.exceptions.exception.NotFoundException;
import ru.practicum.publicuser.repository.PublicCategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PublicCategoryServiceImpl implements PublicCategoryService {

    private final PublicCategoryRepository categoryRepository;

    @Override
    public List<Category> getCategories(Integer from, Integer size) {

        int defaultFrom = (from == null) ? 0 : from;
        int defaultSize = (size == null) ? 10 : size;

        Pageable pageable = PageRequest.of(defaultFrom / defaultSize, defaultSize);

        log.info("Получили список категорий");

        return categoryRepository.getCategories(pageable);
    }

    @Override
    public Category getCategoryById(Long catId) {
        Optional<Category> foundedCategoryDto = categoryRepository.findById(catId);

        log.info("Получили категорию с id: {}", catId);

        return foundedCategoryDto.orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
    }
}
