package ru.itis.dis403.semestrovka.repositories;

import ru.itis.dis403.semestrovka.models.Topic;
import ru.itis.dis403.semestrovka.models.User;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    public User findById(Long id) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            preparedStatement.setLong(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось найти пользователя с таким id");
        }
    }

    public List<User> getAllUsers() {
        try (Connection connection = DBConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users")) {
            return extractUsers(preparedStatement);
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось вывести всех пользователей");
        }
    }

    public User findByLogin(String login) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE login = ?")) {
            preparedStatement.setString(1, login);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
                return null;
            }
        }
    }

    public User findByEmail(String email) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE email = ?")) {
            preparedStatement.setString(1, email);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
                return null;
            }
        }
    }

    public void save(User user) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO users (login, first_name, last_name, email, phone_number, password_hash, birth_date, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getFirstName());
            preparedStatement.setString(3, user.getLastName());
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.setString(5, user.getPhoneNumber());
            preparedStatement.setString(6, user.getPasswordHash());
            preparedStatement.setDate(7, user.getBirthDate() != null ? Date.valueOf(user.getBirthDate()) : null);
            preparedStatement.setString(8, user.getRole());
            preparedStatement.executeUpdate();
        }
    }
    public void adminUpdate(User user) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE users SET login = ?, first_name = ?, last_name = ?, email = ?, phone_number = ?, birth_date = ?, role = ?, gender = ?, is_banned = ? WHERE id = ?")) {

            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getFirstName());
            preparedStatement.setString(3, user.getLastName());
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.setString(5, user.getPhoneNumber());
            preparedStatement.setDate(6, user.getBirthDate() != null ? Date.valueOf(user.getBirthDate()) : null);
            preparedStatement.setString(7, user.getRole());
            preparedStatement.setString(8, user.getGender());
            preparedStatement.setBoolean(9, user.getIsBanned());
            preparedStatement.setLong(10, user.getId());
            preparedStatement.executeUpdate();
        }
    }

    public void updateProfile(Long userId, String firstName, String lastName, String email, String phone, LocalDate birthDate) throws SQLException {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, phone_number = ?, birth_date = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setDate(4, phone != null ? Date.valueOf(LocalDate.now()) : null);
            stmt.setDate(5, birthDate != null ? Date.valueOf(birthDate) : null);
            stmt.setLong(6, userId);
            stmt.executeUpdate();
        }
    }

    public boolean existsByLogin(String login) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE login = ?")) {
            preparedStatement.setString(1, login);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }

    public boolean existsByEmail(String email) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE email = ?")) {
            preparedStatement.setString(1, email);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }

    public void updatePassword(Long userId, String newPasswordHash) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE users SET password_hash = ? WHERE id = ?")) {

            ps.setString(1, newPasswordHash);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

    public void updateRole(Long userId, String newRole) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement("UPDATE users SET role = ? WHERE id = ?")) {
            ps.setString(1, newRole);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

    public void banUser(Long userId, String reason, LocalDateTime until) throws SQLException {
        String sql = "UPDATE users SET is_banned = TRUE, ban_reason = ?, banned_until = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, reason != null ? reason.trim() : null);
            stmt.setObject(2, until);
            stmt.setLong(3, userId);
            stmt.executeUpdate();
        }
    }

    public void unbanUser(Long userId) throws SQLException {
        String sql = "UPDATE users SET is_banned = FALSE, ban_reason = NULL, banned_until = NULL WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.executeUpdate();
        }
    }

    public void updateAvatar(Long userId, String avatarUrl) throws SQLException {
        String sql = "UPDATE users SET avatar_url = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, avatarUrl);
            stmt.setLong(2, userId);
            stmt.executeUpdate();
        }
    }

    private List<User> extractUsers(PreparedStatement ps) throws SQLException {
        List<User> users = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }
        return users;
    }
    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setLogin(rs.getString("login"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setPasswordHash(rs.getString("password_hash"));

        Date birthDate = rs.getDate("birth_date");
        user.setBirthDate(birthDate != null ? birthDate.toLocalDate() : null);

        user.setRole(rs.getString("role"));
        user.setBanned(rs.getBoolean("is_banned"));
        user.setBanReason(rs.getString("ban_reason"));

        Timestamp bannedUntilTs = rs.getTimestamp("banned_until");
        user.setBannedUntil(bannedUntilTs != null ? bannedUntilTs.toLocalDateTime() : null);

        user.setAvatarUrl(rs.getString("avatar_url"));

        return user;
    }
}