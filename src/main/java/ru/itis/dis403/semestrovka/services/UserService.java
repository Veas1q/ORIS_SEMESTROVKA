package ru.itis.dis403.semestrovka.services;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.itis.dis403.semestrovka.dto.*;
import ru.itis.dis403.semestrovka.models.User;
import ru.itis.dis403.semestrovka.repositories.UserRepository;

import java.sql.SQLException;

public class UserService {

    private UserRepository userRepository = new  UserRepository();
    private BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();

    public User registration(UserRegistrationDTO userRegistrationDTO) throws SQLException {

        if (!isEmailAvailable(userRegistrationDTO.getEmail())){
            throw new IllegalArgumentException("Email already exists");
        }

        if (!isLoginAvailable(userRegistrationDTO.getLogin())){
            throw new IllegalArgumentException("Login already exists");
        }

        User user = new User();
        user.setLogin(userRegistrationDTO.getLogin());
        user.setBirthDate(userRegistrationDTO.getBirthDate());
        user.setEmail(userRegistrationDTO.getEmail());
        user.setFirstName(userRegistrationDTO.getFirstName());
        user.setLastName(userRegistrationDTO.getLastName());
        user.setPhoneNumber(userRegistrationDTO.getPhoneNumber());
        user.setRole("USER");
        user.setPasswordHash(bCrypt.encode(userRegistrationDTO.getPassword()));
        userRepository.save(user);

        return user;
    }

    public User login(UserLoginDTO userLoginDTO) throws SQLException {
        User user = userRepository.findByLogin(userLoginDTO.getLogin());
        if (user != null && bCrypt.matches(userLoginDTO.getPassword(), user.getPasswordHash())) {
            return user;
        }
        throw new IllegalArgumentException("Invalid login or password");
    }

    public User findById(Long id) throws SQLException {
        User user = userRepository.findById(id);
        if (user != null) {
            return user;
        }
        throw new IllegalArgumentException("User not found");
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

    public User userUpdate(UserUpdateDTO user) throws SQLException {
        User userUpdate = userRepository.findById(user.getId());
        if (userUpdate != null) {
            userUpdate.setFirstName(user.getFirstName());
            userUpdate.setLastName(user.getLastName());
            userUpdate.setBirthDate(user.getBirthDate());
            userUpdate.setEmail(user.getEmail());
            userUpdate.setGender(user.getGender());
            userUpdate.setPhoneNumber(user.getPhoneNumber());
            userUpdate.setBirthDate(user.getBirthDate());
            userUpdate.setLogin(user.getLogin());

            userRepository.userUpdate(userUpdate);
            return userUpdate;
        }
        throw new IllegalArgumentException("User not found");
    }

    public User adminUpdate(AdminUpdateDTO user) throws SQLException {
        User userUpdate = userRepository.findById(user.getId());
        if (userUpdate != null) {
            userUpdate.setFirstName(user.getFirstName());
            userUpdate.setLastName(user.getLastName());
            userUpdate.setBirthDate(user.getBirthDate());
            userUpdate.setEmail(user.getEmail());
            userUpdate.setGender(user.getGender());
            userUpdate.setPhoneNumber(user.getPhoneNumber());
            userUpdate.setBirthDate(user.getBirthDate());
            userUpdate.setIsBanned(user.getBanned());
            userUpdate.setRole(user.getRole());
            userUpdate.setLogin(user.getLogin());

            userRepository.adminUpdate(userUpdate);
            return userUpdate;
        }
        throw new IllegalArgumentException("User not found");
    }

    public void changePassword(PasswordChangeDTO dto) throws SQLException {

        User user = userRepository.findById(dto.getUserId());

        if (!bCrypt.matches(dto.getOldPassword(), user.getPasswordHash())) {
            throw new SecurityException("Старый пароль неверен");
        }

        String newPasswordHash = bCrypt.encode(dto.getNewPassword());

        userRepository.updatePassword(dto.getUserId(), newPasswordHash);
    }

    public boolean isLoginAvailable(String login) throws SQLException {
        return userRepository.existsByLogin(login);
    }

    public boolean isEmailAvailable(String email) throws SQLException {
        return userRepository.existsByEmail(email);
    }
}
