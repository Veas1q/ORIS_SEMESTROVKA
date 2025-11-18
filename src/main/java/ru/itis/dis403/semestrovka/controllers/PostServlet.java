package ru.itis.dis403.semestrovka.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.dis403.semestrovka.models.Post;
import ru.itis.dis403.semestrovka.models.User;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/post/*")
public class PostServlet extends BaseServlet {

    @Override
    public void init() {
        super.init();
        postService.setServletContext(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        // === РЕДАКТИРОВАНИЕ ПОСТА: показ формы ===
        if (pathInfo != null && pathInfo.matches("/\\d+/edit")) {
            Long postId = Long.parseLong(pathInfo.split("/")[1]);
            Long userId = (Long) req.getSession().getAttribute("userId");
            String role = (String) req.getSession().getAttribute("userRole");

            try {
                Post post = postService.getPostById(postId);
                if (post == null) {
                    resp.sendError(404, "Пост не найден");
                    return;
                }

                boolean canEdit = userId != null &&
                        (post.getUserId().equals(userId) || "ADMIN".equals(role) || "MODERATOR".equals(role));

                if (!canEdit) {
                    resp.sendError(403, "Доступ запрещён");
                    return;
                }

                req.setAttribute("post", post);
                req.getRequestDispatcher("/WEB-INF/views/edit-post.ftlh").forward(req, resp);

            } catch (SQLException e) {
                throw new ServletException("Ошибка загрузки поста", e);
            }
        } else {
            resp.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        try {
            // === СОЗДАНИЕ ПОСТА (ТОЛЬКО ТЕКСТ) ===
            if ("/create".equals(pathInfo)) {
                Long userId = (Long) req.getSession().getAttribute("userId");
                if (userId == null) {
                    resp.sendError(401, "Не авторизован");
                    return;
                }

                String postText = req.getParameter("postText");
                String topicIdStr = req.getParameter("topicId");

                if (postText == null || postText.trim().isEmpty() ||
                        topicIdStr == null || topicIdStr.trim().isEmpty()) {
                    resp.sendError(400, "Текст поста и ID топика обязательны");
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

            // === УДАЛЕНИЕ ПОСТА ===
            if (pathInfo != null && pathInfo.matches("/\\d+/delete")) {
                Long postId = Long.parseLong(pathInfo.split("/")[1]);
                Long userId = (Long) req.getSession().getAttribute("userId");
                String role = userService.findById(userId).getRole();

                if (userId == null) {
                    resp.sendError(401);
                    return;
                }

                Post post = postService.getPostById(postId);
                if (post == null) {
                    resp.sendError(404);
                    return;
                }

                boolean canDelete = post.getUserId().equals(userId) ||
                        "ADMIN".equals(role) || "MODERATOR".equals(role);

                if (!canDelete) {
                    resp.sendError(403);
                    return;
                }
                postService.deleteReactionsFromPost(postId);
                postService.deletePost(postId);
                resp.sendRedirect(req.getContextPath() + "/topic/" + post.getTopicId());
                return;
            }

            // === РЕДАКТИРОВАНИЕ ПОСТА ===
            if (pathInfo != null && pathInfo.matches("/\\d+/edit")) {
                Long postId = Long.parseLong(pathInfo.split("/")[1]);
                String newText = req.getParameter("postText");
                Long userId = (Long) req.getSession().getAttribute("userId");

                if (newText == null || newText.trim().isEmpty()) {
                    resp.sendError(400, "Текст не может быть пустым");
                    return;
                }

                postService.updatePost(postId, userId, newText.trim());

                Post post = postService.getPostById(postId);
                resp.sendRedirect(req.getContextPath() + "/topic/" + post.getTopicId());
                return;
            }

            // === ЛАЙКИ / ДИЗЛАЙКИ ===
            if (pathInfo != null && pathInfo.matches("/\\d+/reaction")) {
                Long postId = Long.parseLong(pathInfo.split("/")[1]);
                String reaction = req.getParameter("reaction");

                User user = (User) req.getSession().getAttribute("user");
                if (user == null) {
                    resp.setStatus(401);
                    resp.getWriter().write("{\"error\": \"Не авторизован\"}");
                    return;
                }

                if (!"LIKE".equals(reaction) && !"DISLIKE".equals(reaction)) {
                    resp.sendError(400);
                    return;
                }

                postService.toggleReaction(postId, user.getId(), reaction);

                int likes = postService.getReactionCount(postId, "LIKE");
                int dislikes = postService.getReactionCount(postId, "DISLIKE");

                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                String json = """
                        {"likes":%d,"dislikes":%d,"userReaction":"%s"}
                        """.formatted(likes, dislikes, reaction);
                resp.getWriter().write(json);
                return;
            }

            resp.sendError(404);

        } catch (SQLException e) {
            throw new ServletException("Ошибка базы данных", e);
        } catch (NumberFormatException e) {
            resp.sendError(400, "Неверный формат ID");
        }
    }
}