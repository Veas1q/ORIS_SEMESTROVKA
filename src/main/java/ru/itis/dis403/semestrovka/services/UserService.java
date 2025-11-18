package ru.itis.dis403.semestrovka.services;

import org.mindrot.jbcrypt.BCrypt;
import ru.itis.dis403.semestrovka.dto.*;
import ru.itis.dis403.semestrovka.models.User;
import ru.itis.dis403.semestrovka.repositories.UserRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    private boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    public User registration(UserRegistrationDTO userRegistrationDTO) throws Exception {
        if (!isEmailAvailable(userRegistrationDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (!isLoginAvailable(userRegistrationDTO.getLogin())) {
                throw new IllegalArgumentException("Login already exists");
        }
        if (userRegistrationDTO.getPassword().length() < 6) {
            throw new Exception("The password is too short");
        }
        if (!userRegistrationDTO.getPassword().equals(userRegistrationDTO.getPassword2())) {
            throw new Exception("Passwords don't match");
        }
        if (!userRegistrationDTO.getPassword().matches("^[a-zA-Z0-9!@#$%^&*()_+=-]+$")) {
            throw new Exception("Пароль может содержать только латинские буквы, цифры и символы");
        }
        User user = new User();
        user.setLogin(userRegistrationDTO.getLogin());
        user.setBirthDate(userRegistrationDTO.getBirthDate());
        user.setEmail(userRegistrationDTO.getEmail());
        user.setRole("USER");
        user.setPasswordHash(hashPassword(userRegistrationDTO.getPassword()));

        userRepository.save(user);
        return user;
    }

    public User login(UserLoginDTO userLoginDTO) throws SQLException {
        User user = userRepository.findByLogin(userLoginDTO.getLogin());
        if (user != null && checkPassword(userLoginDTO.getPassword(), user.getPasswordHash())) {
            return user;
        }
        throw new IllegalArgumentException("Invalid login or password");
    }

    public User findById(Long id)  {
        User user = userRepository.findById(id);
        if (user != null) {
            return user;
        }
        throw new IllegalArgumentException("User not found");
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public User findByLogin(String login) throws SQLException {
        User user = userRepository.findByLogin(login);
        if (user != null) {
            return user;
        }
        throw new IllegalArgumentException("User not found");
    }

    public User findByEmail(String email) throws SQLException {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return user;
        }
        throw new IllegalArgumentException("User not found");
    }

    public void banUser(Long id, String reason, LocalDateTime until) throws SQLException {
        userRepository.banUser(id, reason, until);
    }

    public void unbanUser(Long id) throws SQLException {
        userRepository.unbanUser(id);
    }

    public void updateAvatar(Long userId, String avatarUrl) throws SQLException {
        userRepository.updateAvatar(userId, avatarUrl);
    }

    public void updateUserRole(Long userId, String role) throws SQLException {
        userRepository.updateRole(userId, role);
    }

    public void userUpdate(Long userId, String firstName, String lastName, String email, String phone, LocalDate birthDate) throws SQLException {
        userRepository.updateProfile(userId, firstName, lastName, email, phone, birthDate);
    }

    public User adminUpdate(User user) throws SQLException {
        User userUpdate = userRepository.findById(user.getId());
        if (userUpdate != null) {
            userUpdate.setLogin(user.getLogin());
            userUpdate.setFirstName(user.getFirstName());
            userUpdate.setLastName(user.getLastName());
            userUpdate.setEmail(user.getEmail());
            userUpdate.setPhoneNumber(user.getPhoneNumber());
            userUpdate.setBirthDate(user.getBirthDate());
            userUpdate.setGender(user.getGender());
            userUpdate.setIsBanned(user.getIsBanned());
            userUpdate.setRole(user.getRole());

            userRepository.adminUpdate(userUpdate);
            return userUpdate;
        }
        throw new IllegalArgumentException("User not found");
    }

    public void changePassword(PasswordChangeDTO dto) throws SQLException {
        User user = userRepository.findById(dto.getUserId());

        if (!checkPassword(dto.getOldPassword(), user.getPasswordHash())) {
            throw new SecurityException("Старый пароль неверен");
        }

        String newPasswordHash = hashPassword(dto.getNewPassword());
        userRepository.updatePassword(dto.getUserId(), newPasswordHash);
    }

    public boolean isLoginAvailable(String login) throws SQLException {
        return userRepository.findByLogin(login) == null;
    }

    public boolean isEmailAvailable(String email) throws SQLException {
        return userRepository.findByEmail(email) == null;
    }
}