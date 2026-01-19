package ru.itis.dis403.semestrovka.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.dis403.semestrovka.models.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/admin/*")
public class AdminServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setAttribute("contextPath", req.getContextPath());

        User user = userService.findById(userId);

        if (!isAdminOrModerator(user)) {
            resp.sendError(403, "Доступ запрещен");
        }

        String path = req.getPathInfo();
        if (path == null || path.equals("/") || path.equals("/users")) {
            List<User> userList = userService.getAllUsers();
            req.setAttribute("users", userList);
            req.setAttribute("currentUser", user);
            req.getRequestDispatcher("/admin-users.ftlh").forward(req, resp);
        } else {
            resp.sendError(404, "Страница не найдена");
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        User user = userService.findById(userId);

        if (!isAdminOrModerator(user)) {

            resp.sendError(403, "Доступ запрещен");

        }

        String action = req.getParameter("action");
        Long targetId = Long.valueOf(req.getParameter("userId"));

        User targetUser = userService.findById(targetId);
        if (targetUser == null) {
            resp.sendError(404);
            return;
        }

        if ("ADMIN".equals(targetUser.getRole())) {
            resp.sendError(403, "Нельзя изменять администратора");
            return;
        }

        try {
            switch (action) {
                case "ban":
                    if (!canBan(user, targetUser)) {
                        resp.sendError(403, "Недостаточно прав");
                        return;
                    }
                    String reason = req.getParameter("reason");
                    String days = req.getParameter("days");
                    LocalDateTime until = days != null && !days.isEmpty() ?
                            LocalDateTime.now().plusDays(Long.parseLong(days)) : null;
                    userService.banUser(targetId, reason, until);
                    break;

                case "unban":
                    if (!canBan(user, targetUser)) {
                        resp.sendError(403);
                        return;
                    }
                    userService.unbanUser(targetId);
                    break;

                case "set_moderator":
                    if (!"ADMIN".equals(user.getRole())) {
                        resp.sendError(403, "Только админ может назначать модераторов");
                        return;
                    }
                    userService.updateUserRole(targetId, "MODERATOR");
                    break;

                case "set_user":
                    if (!"ADMIN".equals(user.getRole())) {
                        resp.sendError(403);
                        return;
                    }
                    userService.updateUserRole(targetId, "USER");
                    break;
            }
            resp.sendRedirect(req.getContextPath() + "/admin/users");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private boolean isAdminOrModerator(User user) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        String role = user.getRole().trim().toUpperCase();
        return "ADMIN".equals(role) || "MODERATOR".equals(role);
    }

    private boolean canBan(User actor, User target) {
        if (!isAdminOrModerator(actor)) return false;
        return !"ADMIN".equals(target.getRole());
    }
}