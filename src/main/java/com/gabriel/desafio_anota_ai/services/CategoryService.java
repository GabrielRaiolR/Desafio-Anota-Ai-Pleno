package com.gabriel.desafio_anota_ai.services;

import com.gabriel.desafio_anota_ai.domain.category.Category;
import com.gabriel.desafio_anota_ai.domain.category.CategoryDTO;
import com.gabriel.desafio_anota_ai.domain.category.exceptions.CategoryNotFoundException;
import com.gabriel.desafio_anota_ai.repositories.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public Category insertCategory(CategoryDTO categoryData) {
        Category newCategory = new Category(categoryData);
        this.repository.save(newCategory);
        return newCategory;
    }

    public List<Category> getAllCategories() {
        return this.repository.findAll();
    }

    public Category updateCategory(String id, CategoryDTO categoryData) {
        Category category = this.repository.findById(id).orElseThrow(CategoryNotFoundException:: new);
        if (!categoryData.description().isEmpty()) category.setTitle(categoryData.title());
        this.repository.save(category);
        return category;
    }

    public void deleteCategory(String id) {
        Category category = this.repository.findById(id).orElseThrow(CategoryNotFoundException:: new);
        this.repository.delete(category);
    }

    public List<Category> getAllByCategoryId(String categoryId) {
        return this.repository.findAll();
    }

    public Category getCategoryById(String id) {
        return this.repository.findById(id).orElseThrow(CategoryNotFoundException:: new);
    }

    public Optional<Category> findCategoryById(String id) {
        return this.repository.findById(id);
    }
}
