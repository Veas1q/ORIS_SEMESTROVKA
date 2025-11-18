package ru.itis.dis403.semestrovka.services;

import ru.itis.dis403.semestrovka.models.Category;
import ru.itis.dis403.semestrovka.repositories.CategoryRepository;

import java.sql.SQLException;
import java.util.List;

public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() throws SQLException {
        return categoryRepository.getAllCategories();
    }

    public Category getCategoryById(Long id) throws SQLException {
        Category category = categoryRepository.getCategoryById(id);
        if (category != null) {
            return category;
        }
        throw new IllegalArgumentException("Category not found");
    }

    public List<Category> searchCategoriesByName(String name) throws SQLException {
        return categoryRepository.getCategoryByName(name);
    }

    public Category createCategory(Category category) throws SQLException {
        List<Category> existing = categoryRepository.getCategoryByName(category.getName());
        if (!existing.isEmpty() || existing == null) {
            throw new IllegalArgumentException("Category with this name already exists");
        }

        categoryRepository.addCategory(category);
        return category;
    }

    public Category updateCategory(Category category) throws SQLException {
        Category existingCategory = categoryRepository.getCategoryById(category.getId());
        if (existingCategory != null) {
            categoryRepository.updateCategory(category);
            return category;
        }
        throw new IllegalArgumentException("Category not found");
    }

    public void deleteCategory(Long id) throws SQLException {
        Category category = categoryRepository.getCategoryById(id);
        if (category != null) {
            categoryRepository.deleteCategory(id);
        } else {
            throw new IllegalArgumentException("Category not found");
        }
    }
}