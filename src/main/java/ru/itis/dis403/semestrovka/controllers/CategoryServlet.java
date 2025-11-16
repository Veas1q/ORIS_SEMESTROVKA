package ru.itis.dis403.semestrovka.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.dis403.semestrovka.models.Category;
import ru.itis.dis403.semestrovka.models.User;
import ru.itis.dis403.semestrovka.services.CategoryService;
import ru.itis.dis403.semestrovka.services.TopicService;

import java.io.IOException;
import java.sql.SQLException;

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
                // Создание категории — только для ADMIN и MODERATOR
                Long userId = (Long) req.getSession().getAttribute("userId");
                if (userId == null) {
                    resp.sendRedirect(req.getContextPath() + "/login");
                    return;
                }

                User user = userService.findById(userId);
                if (user == null || !(user.getRole().equals("ADMIN") || user.getRole().equals("MODERATOR"))) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Доступ запрещён");
                    return;
                }

                // Показываем форму создания категории
                req.getRequestDispatcher("/create-category.ftlh").forward(req, resp);

            } else {
                // Просмотр конкретной категории: /category/5
                Long categoryId = Long.parseLong(pathInfo.substring(1));
                var category = categoryService.getCategoryById(categoryId);

                if (category == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Категория не найдена");
                    return;
                }

                // ПРОВЕРКА ВОЗРАСТНОГО ОГРАНИЧЕНИЯ
                Integer ageRestriction = category.getAgeRestriction();
                Long userId = (Long) req.getSession().getAttribute("userId");

                if (ageRestriction != null && ageRestriction >= 18) {
                    if (userId == null) {
                        // Гость → запрещаем доступ
                        req.setAttribute("error", "Для доступа к этой категории требуется вход в аккаунт (18+).");
                        req.setAttribute("redirectUrl", req.getContextPath() + "/login?returnUrl=" +
                                java.net.URLEncoder.encode(req.getRequestURI(), java.nio.charset.StandardCharsets.UTF_8));
                        req.getRequestDispatcher("/error-18plus.ftlh").forward(req, resp);
                        return;
                    }

                    // Если пользователь авторизован — можно проверить возраст, если он есть в профиле
                    // (Опционально: если у тебя есть поле age в User)
                    User user = userService.findById(userId);
                    if (user.getAge() != null && user.getAge() < 18) {
                        req.setAttribute("error", "Эта категория доступна только пользователям старше 18 лет.");
                        req.getRequestDispatcher("/error-18plus.ftlh").forward(req, resp);
                        return;
                    }
                }

                // Всё ок — показываем категорию
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

        if ("/create".equals(pathInfo)) {
            Long userId = (Long) req.getSession().getAttribute("userId");
            if (userId == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            User user = null;
            try {
                user = userService.findById(userId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (user == null || !(user.getRole().equals("ADMIN") || user.getRole().equals("MODERATOR"))) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Доступ запрещён");
                return;
            }

            String name = req.getParameter("name");
            String description = req.getParameter("description");
            String ageRestrictionStr = req.getParameter("ageRestriction");
            Category category = new Category();
            category.setAgeRestriction(Integer.parseInt(ageRestrictionStr));
            category.setName(name);
            category.setDescription(description);

            Integer ageRestriction = null;
            if (ageRestrictionStr != null && !ageRestrictionStr.isEmpty()) {
                try {
                    ageRestriction = Integer.parseInt(ageRestrictionStr);
                } catch (NumberFormatException e) {
                    // Можно добавить ошибку в модель
                }
            }

            try {
                categoryService.createCategory(category);
                resp.sendRedirect(req.getContextPath() + "/category");
            } catch (SQLException e) {
                throw new ServletException("Ошибка при создании категории", e);
            }
        }
    }
}
