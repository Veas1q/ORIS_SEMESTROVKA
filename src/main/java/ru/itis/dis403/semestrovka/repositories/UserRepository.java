package ru.itis.dis403.semestrovka.repositories;

import ru.itis.dis403.semestrovka.dto.AdminUpdateDTO;
import ru.itis.dis403.semestrovka.dto.UserUpdateDTO;
import ru.itis.dis403.semestrovka.models.User;

import java.sql.*;

public class UserRepository {

    public User findById(Long id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            preparedStatement.setLong(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
                return null;
            }
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
            preparedStatement.setBoolean(9, user.getBanned());
            preparedStatement.setLong(10, user.getId());
            preparedStatement.executeUpdate();
        }
    }

    public void userUpdate(User user) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE users SET login = ?, first_name = ?, last_name = ?, email = ?, phone_number = ?, birth_date = ?, gender = ? WHERE id = ?")) {

            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getFirstName());
            preparedStatement.setString(3, user.getLastName());
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.setString(5, user.getPhoneNumber());
            preparedStatement.setDate(6, user.getBirthDate() != null ? Date.valueOf(user.getBirthDate()) : null);
            preparedStatement.setString(7, user.getGender());
            preparedStatement.setLong(8, user.getId());

            preparedStatement.executeUpdate();
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
        return user;
    }
}