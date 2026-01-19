package ru.itis.dis403.semestrovka.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.dis403.semestrovka.models.Post;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/post/*")
public class PostServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo != null && pathInfo.matches("/\\d+/edit")) {
            Long postId = Long.parseLong(pathInfo.split("/")[1]);

            try {
                Post post = postService.getPostById(postId);
                if (post == null) {
                    resp.sendError(404, "Пост не найден");
                    return;
                }

                if (!hasEditRights(req, post.getUserId())) {
                    resp.sendError(403, "Доступ запрещён");
                    return;
                }

                req.setAttribute("post", post);
                req.setAttribute("topicId", post.getTopicId());
                req.getRequestDispatcher("/edit-post.ftlh").forward(req, resp);

            } catch (SQLException e) {
                throw new ServletException("Ошибка загрузки поста", e);
            }
            return;
        }

        resp.sendError(404);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        try {
            if ("/create".equals(pathInfo)) {
                Long userId = (Long) req.getSession().getAttribute("userId");
                if (userId == null) {
                    resp.sendError(401, "Не авторизован");
                    return;
                }

                String postText = req.getParameter("postText");
                String topicIdStr = req.getParameter("topicId");
                if (postText == null || postText.trim().isEmpty() || topicIdStr == null || topicIdStr.trim().isEmpty()) {
                    resp.sendError(400, "Заполните все поля");
                    return;
                }

                Long topicId = Long.parseLong(topicIdStr);
                Post post = new Post();
                post.setPostText(postText.trim());
                post.setUserId(userId);
                post.setTopicId(topicId);

                postService.createPost(post);
                resp.sendRedirect(req.getContextPath() + "/topic/" + topicId);
                return;
            }

            if (pathInfo != null && pathInfo.matches("/\\d+/delete")) {
                Long postId = Long.parseLong(pathInfo.split("/")[1]);
                Post post = postService.getPostById(postId);
                if (post == null) {
                    resp.sendError(404);
                    return;
                }

                if (!hasDeleteRights(req, post.getUserId())) {
                    resp.sendError(403, "Доступ запрещён");
                    return;
                }

                postService.deleteReactionsFromPost(postId);
                postService.deletePost(postId);
                resp.sendRedirect(req.getContextPath() + "/topic/" + post.getTopicId());
                return;
            }

            if (pathInfo != null && pathInfo.matches("/\\d+/edit")) {
                Long postId = Long.parseLong(pathInfo.split("/")[1]);
                Post post = postService.getPostById(postId);
                if (post == null) {
                    resp.sendError(404);
                    return;
                }

                if (!hasEditRights(req, post.getUserId())) {
                    resp.sendError(403, "Доступ запрещён");
                    return;
                }

                String newText = req.getParameter("postText");
                if (newText == null || newText.trim().isEmpty()) {
                    resp.sendError(400, "Текст не может быть пустым");
                    return;
                }

                postService.updatePost(postId, newText.trim());
                resp.sendRedirect(req.getContextPath() + "/topic/" + post.getTopicId());
                return;
            }

            if (pathInfo != null && pathInfo.matches("/\\d+/reaction")) {
                Long postId = Long.parseLong(pathInfo.split("/")[1]);
                String reaction = req.getParameter("reaction");

                Long userId = (Long) req.getSession().getAttribute("userId");
                if (userId == null) {
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    resp.getWriter().write("{\"success\":false,\"message\":\"Войдите, чтобы поставить реакцию\"}");
                    return;
                }

                if (!"LIKE".equals(reaction) && !"DISLIKE".equals(reaction)) {
                    resp.setContentType("application/json");
                    resp.getWriter().write("{\"success\":false,\"message\":\"Неверная реакция\"}");
                    return;
                }

                postService.toggleReaction(postId, userId, reaction);

                int likes = postService.getReactionCount(postId, "LIKE");
                int dislikes = postService.getReactionCount(postId, "DISLIKE");

                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                String json = String.format(
                        "{\"success\":true,\"likes\":%d,\"dislikes\":%d,\"userReaction\":\"%s\"}",
                        likes, dislikes, reaction != null ? reaction : ""
                );
                resp.getWriter().write(json);
                return;
            }

            resp.sendError(404);

        } catch (SQLException e) {
            throw new ServletException("Ошибка базы данных", e);
        } catch (NumberFormatException e) {
            resp.sendError(400, "Неверный ID");
        }
    }

    private boolean hasEditRights(HttpServletRequest req, Long postOwnerId) {
        Long userId = (Long) req.getSession().getAttribute("userId");

        if (userId == null) return false;
        if (userId.equals(postOwnerId)) return true;
        return false;
    }
    private boolean hasDeleteRights(HttpServletRequest req, Long postOwnerId) {
        Long userId = (Long) req.getSession().getAttribute("userId");
        String role = userService.findById(postOwnerId).getRole();

        if (userId == null) return false;
        if (userId.equals(postOwnerId)) return true;
        return "ADMIN".equals(role) || "MODERATOR".equals(role);
    }
}