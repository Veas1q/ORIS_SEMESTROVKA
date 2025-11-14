package ru.itis.dis403.semestrovka.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.dis403.semestrovka.models.Category;
import ru.itis.dis403.semestrovka.models.Topic;
import ru.itis.dis403.semestrovka.services.PostService;
import ru.itis.dis403.semestrovka.services.TopicService;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/topic/*")
public class TopicServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            req.setAttribute("req", req);
            if (pathInfo.equals("/create")) {
                // Форма создания топика
                req.setAttribute("categories", categoryService.getAllCategories());


                req.getRequestDispatcher("/create-topic.ftlh").forward(req, resp);

            } else if (pathInfo.matches("/\\d+/edit")) {
                // Форма редактирования топика
                Long topicId = Long.parseLong(pathInfo.split("/")[1]);
                Topic topic = topicService.getTopicById(topicId);

                // Проверяем права
                Long userId = (Long) req.getSession().getAttribute("userId");
                if (!topic.getUserId().equals(userId)) {
                    resp.sendError(403, "You can only edit your own topics");
                    return;
                }

                req.setAttribute("topic", topic);
                req.getRequestDispatcher("/edit-topic.ftlh").forward(req, resp);

            } else if (pathInfo.matches("/\\d+/delete")) {
                // Страница подтверждения удаления
                Long topicId = Long.parseLong(pathInfo.split("/")[1]);
                Topic topic = topicService.getTopicById(topicId);
                req.setAttribute("topic", topic);
                req.getRequestDispatcher("/delete-topic.ftlh").forward(req, resp);

            } else {
                // Просмотр топика /topic/123
                Long topicId = Long.parseLong(pathInfo.substring(1));
                Topic topic = topicService.getTopicById(topicId);
                req.setAttribute("topic", topic);
                req.setAttribute("posts", postService.getPostsByTopicId(topicId));
                Category category = categoryService.getCategoryById(topic.getCategoryId());
                req.setAttribute("category", category);
                req.setAttribute("topicAuthor", userService.findById(topic.getUserId()));
                topicService.incrementViewCount(topicId);
                req.getRequestDispatcher("/topic.ftlh").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        } catch (NumberFormatException e) {
            resp.sendError(404, "Topic not found");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();

            if (pathInfo.equals("/create")) {
                // СОЗДАНИЕ топика
                Topic topic = new Topic();
                topic.setTitle(req.getParameter("title"));
                topic.setCategoryId(Long.parseLong(req.getParameter("categoryId")));

                String ageStr = req.getParameter("ageRestriction");
                int age = 0;
                if (ageStr != null && !ageStr.isEmpty()) {
                    try {
                        age = Integer.parseInt(ageStr.replace("+", ""));
                    } catch (NumberFormatException e) {
                        age = 0;
                    }
                }
                topic.setAgeRestriction(age);

                Long userId = (Long) req.getSession().getAttribute("userId");
                topic.setUserId(userId);

                Topic createdTopic = topicService.createTopic(topic);
                resp.sendRedirect(req.getContextPath() + "/topic/" + createdTopic.getId());

            } else if (pathInfo.matches("/\\d+/edit")) {
                // РЕДАКТИРОВАНИЕ топика
                Long topicId = Long.parseLong(pathInfo.split("/")[1]);
                Topic topic = topicService.getTopicById(topicId);

                // Проверяем права
                Long userId = (Long) req.getSession().getAttribute("userId");
                if (!topic.getUserId().equals(userId)) {
                    resp.sendError(403, "You can only edit your own topics");
                    return;
                }

                // Обновляем данные
                topic.setTitle(req.getParameter("title"));
                topic.setCategoryId(Long.parseLong(req.getParameter("categoryId")));

                topicService.updateTopic(topic);
                resp.sendRedirect(req.getContextPath() + "/topic/" + topic.getId());

            } else if (pathInfo.matches("/\\d+/delete")) {
                // УДАЛЕНИЕ топика
                Long topicId = Long.parseLong(pathInfo.split("/")[1]);
                Long userId = (Long) req.getSession().getAttribute("userId");
                String userRole = (String) req.getSession().getAttribute("userRole");

                // Проверяем права
                Topic topic = topicService.getTopicById(topicId);
                boolean canDelete = topic.getUserId().equals(userId) ||
                        "ADMIN".equals(userRole) ||
                        "MODERATOR".equals(userRole);

                if (canDelete) {
                    Long categoryId = topic.getCategoryId();
                    topicService.deleteTopic(topicId);
                    resp.sendRedirect(req.getContextPath() + "/category/" + categoryId);
                } else {
                    resp.sendError(403, "No permission to delete this topic");
                }
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}