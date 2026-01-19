package ru.itis.dis403.semestrovka.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.itis.dis403.semestrovka.dto.UserLoginDTO;
import ru.itis.dis403.semestrovka.dto.UserRegistrationDTO;
import ru.itis.dis403.semestrovka.models.User;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@WebServlet("/auth/*")
public class AuthServlet extends BaseServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        request.setAttribute("contextPath", request.getContextPath());
        if ("/login".equals(path)) {
            request.getRequestDispatcher("/login.ftlh").forward(request, response);
        } else if ("/register".equals(path)) {
            request.getRequestDispatcher("/register.ftlh").forward(request, response);
        } else if ("/logout".equals(path)) {
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        request.setAttribute("req", request);

        if ("/register".equals(path)) {
            handleRegister(request, response);
        } else if ("/login".equals(path)) {
            handleLogin(request, response);
        } else if ("/check-login".equals(path)) {
            try {
                handleCheckLogin(request, response);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else if ("/check-email".equals(path)) {
            try {
                handleCheckEmail(request, response);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setEmail(request.getParameter("email"));
        userRegistrationDTO.setPassword(request.getParameter("password"));
        userRegistrationDTO.setLogin(request.getParameter("login"));
        userRegistrationDTO.setBirthDate(LocalDate.parse(request.getParameter("birth_date")));
        userRegistrationDTO.setPassword2(request.getParameter("password2"));

        try {
            User user = userService.registration(userRegistrationDTO);
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("user", user);
            response.sendRedirect(request.getContextPath() + "/");
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("req", request);
            request.getRequestDispatcher("/register.ftlh").forward(request, response);
        }
    }
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setLogin(request.getParameter("login"));
        dto.setPassword(request.getParameter("password"));

        try {
            User user = userService.login(dto);

            if (user.getIsBanned()) {
                if (user.getBannedUntil() != null && user.getBannedUntil().isBefore(LocalDateTime.now())) {
                    userService.unbanUser(user.getId());
                    user = userService.findById(user.getId());
                } else {
                    request.setAttribute("banReason", user.getBanReason());
                    request.setAttribute("bannedUntil", user.getBannedUntil());
                    request.getRequestDispatcher("/banned.ftlh").forward(request, response);
                    return;
                }
            }

            HttpSession session = request.getSession();
            session.setAttribute("userId", user.getId());
            session.setAttribute("user", user);

            String redirect = (String) session.getAttribute("redirectAfterLogin");
            if (redirect != null) {
                session.removeAttribute("redirectAfterLogin");
                response.sendRedirect(redirect);
            } else {
                response.sendRedirect(request.getContextPath() + "/");
            }

        } catch (Exception e) {
            request.setAttribute("error", "Неверный логин или пароль");
            request.setAttribute("req", request);
            request.getRequestDispatcher("/login.ftlh").forward(request, response);
        }
    }

    private void handleCheckLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        String login = req.getParameter("login");
        boolean available = userService.isLoginAvailable(login);
        resp.setContentType("application/json");
        resp.getWriter().write("{\"available\":" + available + "}");
    }

    private void handleCheckEmail(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        String email = req.getParameter("email");
        boolean available = userService.isEmailAvailable(email);
        resp.setContentType("application/json");
        resp.getWriter().write("{\"available\":" + available + "}");
    }
}

