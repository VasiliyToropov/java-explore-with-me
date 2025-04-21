package ru.practicum.admin.service.category;

import ru.practicum.admin.model.category.Category;
import ru.practicum.admin.model.category.NewCategoryDto;

public interface CategoryService {
    public Category addNewCategory(NewCategoryDto newCategoryDto);

    public void deleteCategory(Long catId);

    public Category updateCategory(NewCategoryDto newCategoryDto, Long catId);

    public Category getCategoryById(Long catId);
}
