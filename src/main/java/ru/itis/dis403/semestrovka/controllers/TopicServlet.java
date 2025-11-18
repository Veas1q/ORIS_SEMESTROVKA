package ru.itis.dis403.semestrovka.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.dis403.semestrovka.models.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/topic/*")
public class TopicServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            req.setAttribute("contextPath", req.getContextPath());

            // === СОЗДАНИЕ ТОПИКА ===
            if ("/create".equals(pathInfo)) {
                req.setAttribute("categories", categoryService.getAllCategories());
                req.getRequestDispatcher("/create-topic.ftlh").forward(req, resp);
                return;
            }

            // === РЕДАКТИРОВАНИЕ ===
            if (pathInfo != null && pathInfo.matches("/\\d+/edit")) {
                Long topicId = Long.parseLong(pathInfo.substring(1, pathInfo.indexOf("/edit")));
                Topic topic = topicService.getTopicById(topicId);
                if (topic == null) {
                    resp.sendError(404, "Топик не найден");
                    return;
                }

                Long userId = (Long) req.getSession().getAttribute("userId");
                if (userId == null) {
                    resp.sendRedirect(req.getContextPath() + "/login");
                    return;
                }

                User user = userService.findById(userId);
                boolean isAuthor = user.getId().equals(topic.getUserId());
                boolean isAdmin = "ADMIN".equals(user.getRole());
                boolean isModerator = "MODERATOR".equals(user.getRole());

                if (!isAuthor && !isAdmin && !isModerator) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Доступ запрещён");
                    return;
                }

                req.setAttribute("topic", topic);
                req.setAttribute("categories", categoryService.getAllCategories());
                req.getRequestDispatcher("/edit-topic.ftlh").forward(req, resp);
                return;
            }

            // === УДАЛЕНИЕ (подтверждение) ===
            if (pathInfo != null && pathInfo.matches("/\\d+/delete")) {
                Long topicId = Long.parseLong(pathInfo.split("/")[1]);
                Topic topic = topicService.getTopicById(topicId);
                if (topic == null) {
                    resp.sendError(404);
                    return;
                }

                Long userId = (Long) req.getSession().getAttribute("userId");
                if (userId == null) {
                    resp.sendRedirect(req.getContextPath() + "/login");
                    return;
                }

                User user = userService.findById(userId);
                boolean canDelete = user.getId().equals(topic.getUserId()) ||
                        "ADMIN".equals(user.getRole()) ||
                        "MODERATOR".equals(user.getRole());

                if (!canDelete) {
                    resp.sendError(403);
                    return;
                }

                req.setAttribute("topic", topic);
                req.getRequestDispatcher("/delete-topic.ftlh").forward(req, resp);
                return;
            }

            // === ПРОСМОТР ТОПИКА ===
            Long topicId = Long.parseLong(pathInfo.substring(1));
            Topic topic = topicService.getTopicById(topicId);
            if (topic == null) {
                resp.sendError(404, "Топик не найден");
                return;
            }

            // Проверка возрастного ограничения
            if (topic.getAgeRestriction() != null && topic.getAgeRestriction() > 0) {
                Long userId = (Long) req.getSession().getAttribute("userId");
                if (userId == null) {
                    resp.sendRedirect(req.getContextPath() + "/login");
                    return;
                }
                User user = userService.findById(userId);
                if (user.getBirthDate() == null || user.getAge() < topic.getAgeRestriction()) {
                    req.setAttribute("requiredAge", topic.getAgeRestriction());
                    req.getRequestDispatcher("/error-18plus.ftlh").forward(req, resp);
                    return;
                }
            }

            List<Post> posts = postService.getPostsByTopicId(topicId);
            for (Post post : posts) {
                Long currentUserId = (Long) req.getSession().getAttribute("userId");
                if (currentUserId != null) {
                    post.setLikedByUser(postService.isReaction(post.getId(), currentUserId, "LIKE"));
                    post.setDislikedByUser(postService.isReaction(post.getId(), currentUserId, "DISLIKE"));
                }
                post.setLikesCount(postService.getReactionCount(post.getId(), "LIKE"));
                post.setDislikesCount(postService.getReactionCount(post.getId(), "DISLIKE"));
            }

            req.setAttribute("topic", topic);
            req.setAttribute("posts", posts);
            req.setAttribute("author", userService.findById(topic.getUserId()));
            req.setAttribute("category", categoryService.getCategoryById(topic.getCategoryId()));
            req.setAttribute("user", req.getSession().getAttribute("user"));

            topicService.incrementViewCount(topicId);
            req.getRequestDispatcher("/topic.ftlh").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        } catch (NumberFormatException e) {
            resp.sendError(404, "Invalid topic ID");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            resp.sendError(404);
            return;
        }

        try {
            // === СОЗДАНИЕ ТОПИКА ===
            if ("/create".equals(pathInfo)) {
                Long userId = (Long) req.getSession().getAttribute("userId");
                if (userId == null) {
                    resp.sendRedirect(req.getContextPath() + "/login");
                    return;
                }

                String title = req.getParameter("title");
                String firstPostText = req.getParameter("firstPostText");
                Long categoryId = Long.parseLong(req.getParameter("categoryId"));
                String ageStr = req.getParameter("ageRestriction");
                boolean pinned = req.getParameter("pinned") != null;
                boolean closed = req.getParameter("closed") != null;

                Topic topic = new Topic();
                topic.setTitle(title);
                topic.setCategoryId(categoryId);
                topic.setUserId(userId);
                topic.setPinned(pinned);
                topic.setClosed(closed);

                if (ageStr != null && !ageStr.trim().isEmpty()) {
                    topic.setAgeRestriction(Integer.parseInt(ageStr.trim()));
                }

                Topic created = topicService.createTopic(topic);
                resp.sendRedirect(req.getContextPath() + "/topic/" + created.getId());
                return;
            }

            // === РЕДАКТИРОВАНИЕ / УДАЛЕНИЕ ===
            String[] parts = pathInfo.substring(1).split("/");
            Long topicId = Long.parseLong(parts[0]);
            Topic topic = topicService.getTopicById(topicId);
            if (topic == null) {
                resp.sendError(404);
                return;
            }

            Long userId = (Long) req.getSession().getAttribute("userId");
            if (userId == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            User user = userService.findById(userId);
            boolean isAuthor = user.getId().equals(topic.getUserId());
            boolean isAdmin = "ADMIN".equals(user.getRole());
            boolean isModerator = "MODERATOR".equals(user.getRole());

            if (!isAuthor && !isAdmin && !isModerator) {
                resp.sendError(403, "Доступ запрещён");
                return;
            }

            if (parts.length > 1) {
                String action = parts[1];

                if ("delete".equals(action)) {
                    try {
                        postService.deleteReactionsByTopicId(topicId);
                        postService.deletePostsByTopicId(topicId);
                        topicService.deleteTopic(topicId);

                        resp.sendRedirect(req.getContextPath() + "/category/" + topic.getCategoryId());
                    } catch (SQLException e) {
                        throw new ServletException("Ошибка при удалении топика: " + topicId, e);
                    }
                    return;
                }

                if ("edit".equals(action)) {
                    topic.setTitle(req.getParameter("title"));
                    topic.setCategoryId(Long.parseLong(req.getParameter("categoryId")));
                    topic.setPinned(req.getParameter("pinned") != null);
                    topic.setClosed(req.getParameter("closed") != null);

                    String ageStr = req.getParameter("ageRestriction");
                    if (ageStr != null && !ageStr.trim().isEmpty()) {
                        topic.setAgeRestriction(Integer.parseInt(ageStr.trim()));
                    } else {
                        topic.setAgeRestriction(null);
                    }

                    topicService.updateTopic(topic);
                    resp.sendRedirect(req.getContextPath() + "/topic/" + topicId);
                    return;
                }
            }

            resp.sendError(404);
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
}