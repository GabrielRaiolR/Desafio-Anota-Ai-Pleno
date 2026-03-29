package com.gabriel.desafio_anota_ai.services;

import com.gabriel.desafio_anota_ai.domain.category.Category;
import com.gabriel.desafio_anota_ai.domain.category.CategoryDTO;
import com.gabriel.desafio_anota_ai.domain.category.exceptions.CategoryNotFoundException;
import com.gabriel.desafio_anota_ai.repositories.CategoryRepository;
import com.gabriel.desafio_anota_ai.services.aws.AwsSnsService;
import com.gabriel.desafio_anota_ai.services.aws.MessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    private final AwsSnsService awsSnsService;

    public CategoryService(CategoryRepository repository,  AwsSnsService awsSnsService) {
        this.repository = repository;
        this.awsSnsService = awsSnsService;
    }

    public Category insertCategory(CategoryDTO categoryData) {
        Category newCategory = new Category(categoryData);

        this.repository.save(newCategory);

        this.awsSnsService.publish(new MessageDTO(newCategory.toString()));

        return newCategory;
    }

    public List<Category> getAllCategories() {
        return this.repository.findAll();
    }

    public Category updateCategory(String id, CategoryDTO categoryData) {
        Category category = this.repository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);

        if(!categoryData.title().isEmpty()) category.setTitle(categoryData.title());
        if(!categoryData.description().isEmpty()) category.setDescription(categoryData.description());

        this.awsSnsService.publish(new MessageDTO(category.toString()));

        this.repository.save(category);

        return category;
    }

    public void deleteCategory(String id) {
        Category category = this.repository.findById(id).orElseThrow(CategoryNotFoundException::new);
        this.repository.delete(category);
        this.awsSnsService.publish(new MessageDTO(category.deleteToString()));
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
