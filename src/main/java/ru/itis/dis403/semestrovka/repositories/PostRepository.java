package ru.itis.dis403.semestrovka.repositories;

import ru.itis.dis403.semestrovka.models.Category;
import ru.itis.dis403.semestrovka.models.Post;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostRepository {

    public List<Post> getAllPostFromTopic(Long id)  {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM posts WHERE topic_id = ? ORDER BY created_at ASC");) {

            preparedStatement.setLong(1, id);

            return extractPosts(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при получении постов топика");
        }
    }

    public List<Post> getAllPostsByUserId(Long id)  {
        try (Connection connection = DBConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("select * from posts where user_id = ?")) {
            preparedStatement.setLong(1, id);
            return extractPosts(preparedStatement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Не удалось найти пост");
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

    public Post addPost(Post post) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO posts (user_id, topic_id, post_text) VALUES (?, ?, ?) RETURNING id")) {
            preparedStatement.setLong(1, post.getUserId());
            preparedStatement.setLong(2, post.getTopicId());
            preparedStatement.setString(3, post.getPostText());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    post.setId(rs.getLong("id"));
                }
            }
            return post;
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

    public void deleteReactionsFromCategory(Long id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM posts WHERE category_id = ?")) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        }
    }

    public void deleteReactionsFromPost(Long postId) {
        try (Connection connection = DBConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("delete from post_reactions where post_id = ? ")) {
            preparedStatement.setLong(1, postId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Post> extractPosts(PreparedStatement ps) {
        List<Post> posts = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                posts.add(mapPost(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(" Не удалось получить посты");
        }
        return posts;
    }

    public void toggleLike(Long postId, Long userId) {
        String sqlCheck = "SELECT reaction_type FROM post_reactions WHERE post_id = ? AND user_id = ?";
        String sqlInsert = "INSERT INTO post_reactions (post_id, user_id, reaction_type) VALUES (?, ?, 'LIKE')";
        String sqlUpdate = "UPDATE post_reactions SET reaction_type = 'LIKE' WHERE post_id = ? AND user_id = ?";
        String sqlDelete = "DELETE FROM post_reactions WHERE post_id = ? AND user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement check = conn.prepareStatement(sqlCheck)) {

            check.setLong(1, postId);
            check.setLong(2, userId);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                String current = rs.getString("reaction_type");
                if ("LIKE".equals(current)) {
                    // Уже лайкнул — убираем
                    try (PreparedStatement delete = conn.prepareStatement(sqlDelete)) {
                        delete.setLong(1, postId);
                        delete.setLong(2, userId);
                        delete.executeUpdate();
                    }
                } else {
                    // Был дизлайк — меняем на лайк
                    try (PreparedStatement update = conn.prepareStatement(sqlUpdate)) {
                        update.setLong(1, postId);
                        update.setLong(2, userId);
                        update.executeUpdate();
                    }
                }
            } else {
                // Ничего не было — ставим лайк
                try (PreparedStatement insert = conn.prepareStatement(sqlInsert)) {
                    insert.setLong(1, postId);
                    insert.setLong(2, userId);
                    insert.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(" Не удалось получить посты");
        }
    }

    public int getLikesCount(Long postId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM post_reactions WHERE post_id = ? AND reaction_type = 'LIKE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, postId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public boolean isLikedByUser(Long postId, Long userId) throws SQLException {
        String sql = "SELECT 1 FROM post_reactions WHERE post_id = ? AND user_id = ? AND reaction_type = 'LIKE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, postId);
            ps.setLong(2, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    public void deleteAttachmentsByTopicId(Long topicId) throws SQLException {
        String sql = "DELETE FROM attachments WHERE post_id IN (SELECT id FROM posts WHERE topic_id = ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, topicId);
            stmt.executeUpdate();
        }
    }

    public void toggleReaction(Long postId, Long userId, String reactionType) throws SQLException {
        String checkSql = "SELECT reaction_type FROM post_reactions WHERE post_id = ? AND user_id = ?";
        String insertSql = "INSERT INTO post_reactions (post_id, user_id, reaction_type) VALUES (?, ?, ?)";
        String updateSql = "UPDATE post_reactions SET reaction_type = ? WHERE post_id = ? AND user_id = ?";
        String deleteSql = "DELETE FROM post_reactions WHERE post_id = ? AND user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setLong(1, postId);
            check.setLong(2, userId);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                String current = rs.getString(1);
                if (current.equals(reactionType)) {
                    // Уже стоит — убираем
                    try (PreparedStatement delete = conn.prepareStatement(deleteSql)) {
                        delete.setLong(1, postId);
                        delete.setLong(2, userId);
                        delete.executeUpdate();
                    }
                } else {
                    // Меняем на другой
                    try (PreparedStatement update = conn.prepareStatement(updateSql)) {
                        update.setString(1, reactionType);
                        update.setLong(2, postId);
                        update.setLong(3, userId);
                        update.executeUpdate();
                    }
                }
            } else {
                // Ничего нет — ставим
                try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
                    insert.setLong(1, postId);
                    insert.setLong(2, userId);
                    insert.setString(3, reactionType);
                    insert.executeUpdate();
                }
            }
        }
    }

    public int getReactionCount(Long postId, String reactionType) throws SQLException {
        String sql = "SELECT COUNT(*) FROM post_reactions WHERE post_id = ? AND reaction_type = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, postId);
            ps.setString(2, reactionType);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public boolean isReaction(Long postId, Long userId, String reactionType) throws SQLException {
        String sql = "SELECT 1 FROM post_reactions WHERE post_id = ? AND user_id = ? AND reaction_type = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, postId);
            ps.setLong(2, userId);
            ps.setString(3, reactionType);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }


    public void deletePostsByTopicId(Long topicId) {
        String sql = "DELETE FROM posts WHERE topic_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, topicId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при удалении постов топика: " + topicId, e);
        }
    }

    public void deleteReactionsByPostId(Long postId) {
        String sql = "DELETE FROM post_reactions WHERE post_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, postId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении реакций постов: " + postId, e);
        }
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
