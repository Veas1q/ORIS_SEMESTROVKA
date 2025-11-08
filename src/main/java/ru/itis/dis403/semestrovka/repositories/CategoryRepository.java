package ru.itis.dis403.semestrovka.repositories;

import ru.itis.dis403.semestrovka.models.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {

    public List<Category> getAllCategories() throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM categories")) {

            return extractCategories(preparedStatement);
        }
    }

    public Category getCategoryById(Long id) throws SQLException {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM categories WHERE id = ?")) {

            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapCategory(resultSet);
                }
            }
        }

        return null;
    }

    public List<Category> getCategoryByName(String name) throws SQLException {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM categories WHERE name LIKE ?");) {

            preparedStatement.setString(1, "%" + name + "%");
            return extractCategories(preparedStatement);
        }
    }

    public void addCategory(Category category) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO categories (name, description, age_restriction) VALUES (?, ?, ?)")) {

            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.setInt(3, category.getAgeRestriction());
            preparedStatement.executeUpdate();
        }
    }

    public void updateCategory(Category category) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("update categories set name = ?, description = ?, age_restriction = ?  WHERE id = ?")) {

            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.setInt(3, category.getAgeRestriction());
            preparedStatement.setLong(4, category.getId());
            preparedStatement.executeUpdate();
        }
    }

    public void deleteCategory(Long id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM categories WHERE id = ?")) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        }
    }

    private List<Category> extractCategories(PreparedStatement ps) throws SQLException {
        List<Category> categories = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(mapCategory(rs));
            }
        }
        return categories;
    }

    public static Category mapCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getLong("id"));
        category.setName(rs.getString("name"));
        category.setDescription(rs.getString("description"));
        category.setAgeRestriction(rs.getInt("age_restriction"));
        return category;
    }
}
