package ru.itis.dis403.semestrovka.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.dis403.semestrovka.models.Category;
import ru.itis.dis403.semestrovka.models.Post;
import ru.itis.dis403.semestrovka.models.Topic;
import ru.itis.dis403.semestrovka.models.User;
import ru.itis.dis403.semestrovka.services.PostService;
import ru.itis.dis403.semestrovka.services.TopicService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/topic/*")
public class TopicServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            req.setAttribute("contextPath", req.getContextPath());

            if (pathInfo.equals("/create")) {
                // Форма создания топика
                req.setAttribute("categories", categoryService.getAllCategories());


                req.getRequestDispatcher("/create-topic.ftlh").forward(req, resp);

            } else if (pathInfo.matches("/\\d+/edit")) {
                Long topicId = Long.parseLong(req.getPathInfo().substring(1).replace("/edit", ""));
                Topic topic = topicService.getTopicById(topicId);
                User user = (User) req.getSession().getAttribute("user");

                if (topic == null || (user == null || (!topic.getUserId().equals(user.getId()) && ( user.getRole() != "ADMIN" || user.getRole() != "MODERATOR"    )))) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                req.setAttribute("topic", topic);
                req.setAttribute("contextPath", req.getContextPath());

                 req.setAttribute("categories", categoryService.getAllCategories());

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

                if (topic == null) {
                    resp.sendError(404);
                    return;
                }
                List<Post> posts = postService.getPostsByTopicId(topicId);
                for (Post post : posts) {
                    post.setAttachmentsList(attachmentService.findByPostId(post.getId()));
                }
                req.setAttribute("posts", posts);

                req.setAttribute("author", userService.findById(topic.getUserId()));

                req.setAttribute("topic", topic);

                User currentUser = (User) req.getSession().getAttribute("user");

                for (Post post : posts) {
                    if (currentUser != null) {
                        post.setLikedByUser(postService.isReaction(post.getId(), currentUser.getId(), "LIKE"));
                        post.setDislikedByUser(postService.isReaction(post.getId(), currentUser.getId(), "DISLIKE"));
                    }
                    post.setLikesCount(postService.getReactionCount(post.getId(), "LIKE"));
                    post.setDislikesCount(postService.getReactionCount(post.getId(), "DISLIKE"));
                }

                req.setAttribute("user", currentUser);

                req.setAttribute("session", req.getSession());

// Автор топика
                User topicAuthor = userService.findById(topic.getUserId());
                req.setAttribute("topicAuthor", topicAuthor);
// Категория
                Category category = categoryService.getCategoryById(topic.getCategoryId());
                req.setAttribute("category", category);

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
                User user = userService.findById(userId);
                String userRole = user.getRole();

                // Проверяем права
                Topic topic = topicService.getTopicById(topicId);
                boolean canDelete = topic.getUserId().equals(userId) ||
                        "ADMIN".equals(userRole) ||
                        "MODERATOR".equals(userRole);

                if (canDelete) {
                    Long categoryId = topic.getCategoryId();
                    postService.deleteReactionsByPostId(topicId);
                    postService.deletePostsByTopicId(topic.getId());
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