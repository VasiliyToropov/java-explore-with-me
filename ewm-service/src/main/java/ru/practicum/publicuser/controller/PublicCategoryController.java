package ru.practicum.publicuser.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin.model.category.Category;
import ru.practicum.publicuser.service.PublicCategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class PublicCategoryController {

    private final PublicCategoryService publicCategoryService;

    @GetMapping
    public List<Category> getCategories(@RequestParam(required = false) Integer from, @RequestParam(required = false) Integer size) {
        return publicCategoryService.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    public Category getCategoryById(@PathVariable Long catId) {
        return publicCategoryService.getCategoryById(catId);
    }
}
