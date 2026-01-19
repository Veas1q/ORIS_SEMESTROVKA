package ru.itis.dis403.semestrovka.repositories;

import ru.itis.dis403.semestrovka.models.Category;
import ru.itis.dis403.semestrovka.models.Topic;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class TopicRepository {

    public List<Topic> findAll() throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM topics ORDER BY created_at DESC")) {
            return extractTopics(preparedStatement);
        }
    }

    public List<Topic> findByCategoryId(Long categoryId) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM topics WHERE category_id = ? ORDER BY created_at DESC")) {
            preparedStatement.setLong(1, categoryId);
            return extractTopics(preparedStatement);
        }
    }

    public Topic findById(Long id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM topics WHERE id = ?")) {
            preparedStatement.setLong(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return mapTopic(rs);
                }
                return null;
            }
        }
    }

    public List<Topic> findRecent(int limit) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM topics ORDER BY created_at DESC LIMIT ?")) {
            preparedStatement.setInt(1, limit);
            return extractTopics(preparedStatement);
        }
    }

    public List<Topic> findPinned() throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM topics WHERE is_pinned = true ORDER BY created_at DESC")) {
            return extractTopics(preparedStatement);
        }
    }

    public List<Topic> findByUserId(Long userId) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM topics WHERE user_id = ? ORDER BY created_at DESC")) {
            preparedStatement.setLong(1, userId);
            return extractTopics(preparedStatement);
        }
    }

    public Topic addTopic(Topic topic) throws SQLException {
        String sql = """
        INSERT INTO topics (title, category_id, user_id, age_restriction)
        VALUES (?, ?, ?, ?)
        RETURNING id
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, topic.getTitle());
            ps.setLong(2, topic.getCategoryId());
            ps.setLong(3, topic.getUserId());
            ps.setInt(4, topic.getAgeRestriction());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    topic.setId(rs.getLong("id"));
                } else {
                    throw new SQLException("Не удалось создать топик - id не вернулся");
                }
            }
        }
        return topic;
    }

    public void updateTopic(Topic topic) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE topics SET title = ?, category_id = ?, age_restriction = ? WHERE id = ?")) {
            preparedStatement.setString(1, topic.getTitle());
            preparedStatement.setLong(2, topic.getCategoryId());
            if (topic.getAgeRestriction() != null   ) {
                preparedStatement.setInt(3, topic.getAgeRestriction());
            } else {
                preparedStatement.setInt(3, 0);
            }
            preparedStatement.setLong(4, topic.getId());
            preparedStatement.executeUpdate();
        }
    }

    public List<Topic> searchTopicsByTitle(String query) throws SQLException {
        String sql = """
        SELECT id, title, created_at, category_id 
        FROM topics 
        WHERE LOWER(title) LIKE LOWER(?) 
        ORDER BY created_at DESC 
        LIMIT 50
        """;

        String likePattern = "%" + query.trim() + "%";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, likePattern);

            try (ResultSet rs = stmt.executeQuery()) {
                List<Topic> results = new ArrayList<>();

                while (rs.next()) {
                    Topic topic = new Topic();

                    topic.setId(rs.getLong("id"));
                    topic.setTitle(rs.getString("title"));
                    topic.setCategoryId(rs.getLong("category_id"));

                    Timestamp timestamp = rs.getTimestamp("created_at");
                    if (timestamp != null) {
                        topic.setCreatedAt(timestamp.toLocalDateTime().toString());
                    } else {
                        topic.setCreatedAt(null);
                    }

                    results.add(topic);
                }
                return results;
            }
        }
    }

    public void deleteTopicsByCategoryId(Long categoryId) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("delete from topics where category_id = ?")) {
            preparedStatement.setLong(1 , categoryId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteTopic(Long id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM topics WHERE id = ?")) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        }
    }

    public void updateViewCount(Long topicId) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE topics SET view_count = view_count + 1 WHERE id = ?")) {
            preparedStatement.setLong(1, topicId);
            preparedStatement.executeUpdate();
        }
    }

    public void togglePinned(Long topicId, boolean pinned, Long pinnedByUserId) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE topics SET is_pinned = ?, pinned_by_user_id = ?, pinned_at = ? WHERE id = ?")) {
            preparedStatement.setBoolean(1, pinned);
            preparedStatement.setObject(2, pinnedByUserId);
            preparedStatement.setTimestamp(3, pinned ? Timestamp.valueOf(LocalDateTime.now()) : null);
            preparedStatement.setLong(4, topicId);
            preparedStatement.executeUpdate();
        }
    }

    public void toggleClosed(Long topicId, boolean closed, Long closedByUserId) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE topics SET is_closed = ?, closed_by_user_id = ?, closed_at = ? WHERE id = ?")) {
            preparedStatement.setBoolean(1, closed);
            preparedStatement.setObject(2, closedByUserId);
            preparedStatement.setTimestamp(3, closed ? Timestamp.valueOf(LocalDateTime.now()) : null);
            preparedStatement.setLong(4, topicId);
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

    private Topic mapTopic(ResultSet rs) throws SQLException {
        Topic topic = new Topic();
        topic.setId(rs.getLong("id"));
        topic.setTitle(rs.getString("title"));
        topic.setCategoryId(rs.getLong("category_id"));
        topic.setUserId(rs.getLong("user_id"));
        topic.setPinned(rs.getBoolean("is_pinned"));
        topic.setClosed(rs.getBoolean("is_closed"));
        topic.setViewCount(rs.getInt("view_count"));
        topic.setAgeRestriction(rs.getInt("age_restriction"));
        topic.setPinnedByUserId(rs.getLong("pinned_by_user_id"));
        topic.setPinnedAt(getNullableTimestamp(rs, "pinned_at"));
        topic.setClosedByUserId(rs.getLong("closed_by_user_id"));
        topic.setClosedAt(getNullableTimestamp(rs, "closed_at"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            topic.setCreatedAt(ts.toLocalDateTime().format(fmt));
        } else {
            topic.setCreatedAt("—");
        }
        return topic;
    }

    private static LocalDateTime getNullableTimestamp(ResultSet rs, String column) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(column);
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
}