package ru.itis.dis403.semestrovka.repositories;

import ru.itis.dis403.semestrovka.models.Category;
import ru.itis.dis403.semestrovka.models.Post;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostRepository {

    public List<Post> getAllPostFromTopic(Long id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM posts WHERE topic_id = ? ORDER BY created_at ASC");) {

            preparedStatement.setLong(1, id);

            return extractPosts(preparedStatement);
        }
    }

    public Post getPostById(Long id) throws SQLException {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM posts WHERE id = ?")) {

            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapPost(resultSet);
                }
            }
        }

        return null;
    }

    public Post getFirstPostInTopic(Long topicId) throws SQLException {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM posts WHERE topic_id = ? AND is_first_post = true");) {

            preparedStatement.setLong(1, topicId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapPost(resultSet);
                }
            }
        }
        return null;
    }

    public void addPost(Post post) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO posts (user_id, topic_id, post_text, is_first_post) VALUES (?, ?, ?, ?)"))  {
            preparedStatement.setLong(1, post.getUserId());
            preparedStatement.setLong(2, post.getTopicId());
            preparedStatement.setString(3, post.getPostText());
            preparedStatement.setBoolean(4, post.getFirstPost());

            preparedStatement.executeUpdate();
        }
    }

    public void updatePost(Post post) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE posts SET post_text = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?")) {

            preparedStatement.setString(1, post.getPostText());
            preparedStatement.setLong(2, post.getId());
            preparedStatement.executeUpdate();
        }
    }

    public void setPostPinned(Long postId, boolean pinned, Long pinnedByUserId) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE posts SET is_pinned_in_topic = ?, pinned_by_user_id = ?, pinned_at = ? WHERE id = ?")) {

            preparedStatement.setBoolean(1, pinned);
            preparedStatement.setObject(2, pinnedByUserId);
            preparedStatement.setTimestamp(3, pinned ? Timestamp.valueOf(LocalDateTime.now()) : null);
            preparedStatement.setLong(4, postId);
            preparedStatement.executeUpdate();
        }
    }

    public void deletePost(Long id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM posts WHERE id = ?")) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        }
    }

    private List<Post> extractPosts(PreparedStatement ps) throws SQLException {
        List<Post> posts = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                posts.add(mapPost(rs));
            }
        }
        return posts;
    }

    private static Post mapPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setUserId(rs.getLong("user_id"));
        post.setTopicId(rs.getLong("topic_id"));
        post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        post.setUpdatedAt(getNullableTimestamp(rs, "updated_at"));
        post.setPostText(rs.getString("post_text"));
        post.setFirstPost(rs.getBoolean("is_first_post"));
        post.setPinnedInTopic(rs.getBoolean("is_pinned_in_topic"));
        post.setPinnedByUserId(rs.getLong("pinned_by_user_id"));
        post.setPinnedAt(getNullableTimestamp(rs, "pinned_at"));
        return post;
    }

    private static LocalDateTime getNullableTimestamp(ResultSet rs, String column) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(column);
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
}
