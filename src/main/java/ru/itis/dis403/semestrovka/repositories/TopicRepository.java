package ru.itis.dis403.semestrovka.repositories;

import ru.itis.dis403.semestrovka.models.Category;
import ru.itis.dis403.semestrovka.models.Topic;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.itis.dis403.semestrovka.utils.ResultSetMapper.getNullableTimestamp;

public class TopicRepository {

    public List<Category> getAllTopics() throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM topics order by id desc;");) {

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

    private List<Topic> extractTopics(PreparedStatement ps) throws SQLException {
        List<Topic> topics = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                topics.add(mapTopic(rs));
            }
        }
        return topics;
    }

    public static Topic mapTopic(ResultSet rs) throws SQLException {
        Topic topic = new Topic();
        topic.setId(rs.getLong("id"));
        topic.setTitle(rs.getString("title"));
        topic.setCategoryId(rs.getLong("category_id"));
        topic.setUserId(rs.getLong("user_id"));
        topic.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        topic.setPinned(rs.getBoolean("is_pinned"));
        topic.setClosed(rs.getBoolean("is_closed"));
        topic.setViewCount(rs.getInt("view_count"));
        topic.setAgeRestriction(rs.getInt("age_restriction"));
        topic.setPinnedByUserId(rs.getLong("pinned_by_user_id"));
        topic.setPinnedAt(getNullableTimestamp(rs, "pinned_at"));
        topic.setClosedByUserId(rs.getLong("closed_by_user_id"));
        topic.setClosedAt(getNullableTimestamp(rs, "closed_at"));
        return topic;
    }

    private static LocalDateTime getNullableTimestamp(ResultSet rs, String column) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(column);
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
}
