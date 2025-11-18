package ru.itis.dis403.semestrovka.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.dis403.semestrovka.models.Category;
import ru.itis.dis403.semestrovka.models.Post;
import ru.itis.dis403.semestrovka.models.Topic;
import ru.itis.dis403.semestrovka.models.User;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/category/*")
public class CategoryServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            req.setAttribute("contextPath", req.getContextPath());

            if (pathInfo == null || pathInfo.equals("/")) {
                req.setAttribute("categories", categoryService.getAllCategories());
                req.getRequestDispatcher("/categories.ftlh").forward(req, resp);

            } else if (pathInfo.equals("/create")) {
                Long userId = (Long) req.getSession().getAttribute("userId");
                if (userId == null) {
                    resp.sendRedirect(req.getContextPath() + "/login");
                    return;
                }
                User user = userService.findById(userId);
                if (user == null || !("ADMIN".equals(user.getRole()) || "MODERATOR".equals(user.getRole()))) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                req.getRequestDispatcher("/create-category.ftlh").forward(req, resp);

            } else {
                Long categoryId = Long.parseLong(pathInfo.substring(1));
                Category category = categoryService.getCategoryById(categoryId);
                if (category == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Категория не найдена");
                    return;
                }

                // 18+ проверка
                Integer ageRestriction = category.getAgeRestriction();
                Long userId = (Long) req.getSession().getAttribute("userId");
                if (ageRestriction != null && ageRestriction >= 18) {
                    if (userId == null) {
                        req.setAttribute("error", "Для доступа к этой категории требуется вход в аккаунт (18+).");
                        req.setAttribute("redirectUrl", req.getContextPath() + "/login?returnUrl=" +
                                URLEncoder.encode(req.getRequestURI(), StandardCharsets.UTF_8));
                        req.getRequestDispatcher("/error-18plus.ftlh").forward(req, resp);
                        return;
                    }
                    User user = userService.findById(userId);
                    if (user.getAge() != null && user.getAge() < 18) {
                        req.setAttribute("error", "Эта категория доступна только пользователям старше 18 лет.");
                        req.getRequestDispatcher("/error-18plus.ftlh").forward(req, resp);
                        return;
                    }
                }

                req.setAttribute("category", category);
                req.setAttribute("topics", topicService.getTopicsByCategoryId(categoryId));
                req.getRequestDispatcher("/category.ftlh").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        } catch (NumberFormatException e) {
            resp.sendError(404, "Category not found");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        User currentUser = (User) req.getSession().getAttribute("user");
        Long userId = (Long) req.getSession().getAttribute("userId");

        // === СОЗДАНИЕ КАТЕГОРИИ ===
        if ("/create".equals(pathInfo)) {
            if (userId == null || currentUser == null ||
                    !("ADMIN".equals(currentUser.getRole()) || "MODERATOR".equals(currentUser.getRole()))) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            String name = req.getParameter("name");
            String description = req.getParameter("description");
            String ageRestrictionStr = req.getParameter("ageRestriction");

            if (name == null || name.trim().isEmpty()) {
                req.setAttribute("error", "Название категории обязательно");
                req.getRequestDispatcher("/create-category.ftlh").forward(req, resp);
                return;
            }

            Category category = new Category();
            category.setName(name.trim());
            category.setDescription(description != null ? description.trim() : null);

            if (ageRestrictionStr != null && !ageRestrictionStr.trim().isEmpty()) {
                try {
                    int age = Integer.parseInt(ageRestrictionStr.trim());
                    category.setAgeRestriction(age > 0 ? age : 0);
                } catch (NumberFormatException e) {
                    category.setAgeRestriction(0);
                }
            } else {
                category.setAgeRestriction(0);
            }

            try {
                categoryService.createCategory(category);
                resp.sendRedirect(req.getContextPath() + "/category");
            } catch (SQLException e) {
                req.setAttribute("error", "Ошибка при создании категории");
                req.getRequestDispatcher("/create-category.ftlh").forward(req, resp);
            }
            return;
        }

        // === УДАЛЕНИЕ КАТЕГОРИИ (ТОЛЬКО АДМИН) ===
        if (pathInfo != null && pathInfo.matches("/\\d+/delete")) {
            if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Только администратор может удалять категории");
                return;
            }

            Long categoryId = Long.parseLong(pathInfo.split("/")[1]);

            try {
                List<Topic> topicList = topicService.getTopicsByCategoryId(categoryId);
                for (Topic topic : topicList) {
                    List<Post> postList = postService.getPostsByTopicId(topic.getId());
                    for (Post post : postList) {
                        postService.deleteReactionsFromPost(post.getId());
                        postService.deletePost(post.getId());
                    }
                }
                topicService.deleteTopicsByCategoryId(categoryId);
                categoryService.deleteCategory(categoryId);

                resp.sendRedirect(req.getContextPath() + "/category");
            } catch (SQLException e) {
                throw new ServletException("Ошибка при удалении категории ID=" + categoryId, e);
            }
            return;
        }

        resp.sendError(404);
    }
}