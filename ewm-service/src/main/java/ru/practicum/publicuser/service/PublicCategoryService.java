package ru.practicum.publicuser.service;

import ru.practicum.admin.model.category.Category;

import java.util.List;

public interface PublicCategoryService {
    List<Category> getCategories(Integer from, Integer size);

    Category getCategoryById(Long catId);
}
