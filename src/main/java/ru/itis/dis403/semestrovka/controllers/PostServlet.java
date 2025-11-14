package ru.itis.dis403.semestrovka.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.dis403.semestrovka.models.Post;
import ru.itis.dis403.semestrovka.models.User;
import ru.itis.dis403.semestrovka.services.PostService;
import ru.itis.dis403.semestrovka.services.TopicService;
import ru.itis.dis403.semestrovka.services.UserService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/post/*")
public class PostServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            String pathInfo = req.getPathInfo();
            req.setAttribute("contextPath", req.getContextPath());

            if (pathInfo.equals("/create")) {

                req.getRequestDispatcher("create-post.ftlh").forward(req, resp);
            } else if (pathInfo.matches("/\\d+/delete")) {
                Long postId = Long.parseLong(pathInfo.split("/")[1]);
                Post post = postService.getPostById(postId);

                String role = (String) req.getSession().getAttribute("userRole");
                Long userId = (Long) req.getSession().getAttribute("userId");
               
                req.getRequestDispatcher("delete-post.ftlh").forward(req, resp);
            } else if (pathInfo.matches("/\\d+/edit")) {
                Long postId = Long.parseLong(pathInfo.split("/")[1]);
                Post post = postService.getPostById(postId);

                String role = (String) req.getSession().getAttribute("userRole");
                Long userId = (Long) req.getSession().getAttribute("userId");

                req.getRequestDispatcher("edit-post.ftlh").forward(req, resp);
            } else {
                resp.sendError(404);
            }


        } catch (SQLException e) {
            throw new ServletException(e);
        }

    }@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();

        try {
            if ("/create".equals(pathInfo)) {
                // === СОЗДАНИЕ ПОСТА ===
                Long userId = (Long) req.getSession().getAttribute("userId");
                if (userId == null) {
                    resp.sendError(401, "Не авторизован");
                    return;
                }
                User currentUser = (User) req.getSession().getAttribute("user");
                req.setAttribute("user", currentUser);

                String topicIdStr = req.getParameter("topicId");
                String postText = req.getParameter("postText");

                if (topicIdStr == null || postText == null || postText.trim().isEmpty()) {
                    resp.sendError(400, "Неверные данные");
                    return;
                }

                Long topicId = Long.parseLong(topicIdStr);
                Post post = new Post();
                post.setPostText(postText.trim());
                post.setUserId(userId);           // берём из сессии!
                post.setTopicId(topicId);         // берём из формы
                postService.createPost(post);     // создаём пост
                resp.sendRedirect(req.getContextPath() + "/topic/" + topicId);

            } else if (pathInfo != null && pathInfo.matches("/\\d+/delete")) {
                // === УДАЛЕНИЕ ПОСТА ===
                Long postId = Long.parseLong(pathInfo.split("/")[1]);
                User user = (User) req.getSession().getAttribute("user");

                if (user == null) {
                    resp.sendError(401);
                    return;
                }

                postService.deletePost(postId, user);
                // Редирект обратно на топик (можно передать topicId через параметр или из поста)
                Post deletedPost = postService.getPostById(postId); // или сохраните topicId раньше
                resp.sendRedirect(req.getContextPath() + "/topic/" + deletedPost.getTopicId());

            } else if (pathInfo != null && pathInfo.matches("/\\d+/edit")) {
                // === РЕДАКТИРОВАНИЕ ПОСТА ===
                Long postId = Long.parseLong(pathInfo.split("/")[1]);
                String newText = req.getParameter("postText");
                Long userId = (Long) req.getSession().getAttribute("userId");

                if (newText != null && !newText.trim().isEmpty()) {
                    postService.updatePost(postId, userId, newText.trim());
                }

                Post post = postService.getPostById(postId);
                resp.sendRedirect(req.getContextPath() + "/topic/" + post.getTopicId());

            }else if (pathInfo != null && pathInfo.matches("/\\d+/reaction")) {
                // === РЕАКЦИЯ: ЛАЙК / ДИЗЛАЙК ===
                Long postId = Long.parseLong(pathInfo.split("/")[1]);
                String reaction = req.getParameter("reaction"); // "LIKE" или "DISLIKE"

                User user = (User) req.getSession().getAttribute("user");
                if (user == null) {
                    resp.setStatus(401);
                    resp.getWriter().write("{\"error\": \"Не авторизован\"}");
                    return;
                }

                if (!"LIKE".equals(reaction) && !"DISLIKE".equals(reaction)) {
                    resp.sendError(400, "Invalid reaction");
                    return;
                }

                // Переключаем реакцию
                postService.toggleReaction(postId, user.getId(), reaction);

                // Получаем актуальные счётчики
                int likes = postService.getReactionCount(postId, "LIKE");
                int dislikes = postService.getReactionCount(postId, "DISLIKE");

                // Определяем, какая реакция у пользователя сейчас
                String userReaction = null;
                if (postService.isReaction(postId, user.getId(), "LIKE")) {
                    userReaction = "LIKE";
                } else if (postService.isReaction(postId, user.getId(), "DISLIKE")) {
                    userReaction = "DISLIKE";
                }

                // Возвращаем JSON
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                String json = String.format("{\"likes\": %d, \"dislikes\": %d, \"reaction\": %s}",
                        likes, dislikes, userReaction == null ? "null" : "\"" + userReaction + "\"");
                resp.getWriter().write(json);
                return;
            }

        } catch (SQLException e) {
            throw new ServletException("Ошибка базы данных", e);
        } catch (NumberFormatException e) {
            resp.sendError(400, "Неверный ID");
        }
    }
}
