package ru.itis.dis403.semestrovka.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import ru.itis.dis403.semestrovka.models.Post;
import ru.itis.dis403.semestrovka.models.Topic;
import ru.itis.dis403.semestrovka.models.User;
import ru.itis.dis403.semestrovka.models.Attachment;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/post/*")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,     // 1 MB — в памяти
        maxFileSize = 1024 * 1024 * 10,      // 10 MB на файл
        maxRequestSize = 1024 * 1024 * 50,   // 50 MB на весь запрос
        location = "C:\\Users\\Redmi\\Desktop\\Attachments"
)
public class PostServlet extends BaseServlet {

    @Override
    public void init() {
        super.init();
        postService.setServletContext(getServletContext());  // Передаём контекст
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        req.setAttribute("contextPath", req.getContextPath());  // Всегда передаём

        Long userId = (Long) req.getSession().getAttribute("userId");
        String role = (String) req.getSession().getAttribute("userRole");

        // === РЕДАКТИРОВАНИЕ: ПОКАЗАТЬ ФОРМУ ===
        if (pathInfo != null && pathInfo.matches("/\\d+/edit")) {
            Long postId = Long.parseLong(pathInfo.split("/")[1]);
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
                throw new ServletException("Ошибка при загрузке поста", e);
            }

        } else {
            resp.sendError(404, "Действие не найдено");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();

        try {
            if ("/create".equals(pathInfo)) {
                Long userId = (Long) req.getSession().getAttribute("userId");
                if (userId == null) {
                    resp.sendError(401);
                    return;
                }

                Part topicIdPart = req.getPart("topicId");
                String topicIdStr = topicIdPart != null
                        ? new String(topicIdPart.getInputStream().readAllBytes(), "UTF-8").trim()
                        : null;

                Part postTextPart = req.getPart("postText");
                String postText = postTextPart != null
                        ? new String(postTextPart.getInputStream().readAllBytes(), "UTF-8").trim()
                        : null;

                if (topicIdStr == null || topicIdStr.isEmpty() || postText == null || postText.isEmpty()) {
                    resp.sendError(400, "topicId или postText не могут быть пустыми");
                    return;
                }

                Long topicId = Long.parseLong(topicIdStr);
                Topic topic = topicService.getTopicById(topicId);
                if (topic == null) {
                    resp.sendError(404);
                    return;
                }

                Post post = new Post();
                post.setPostText(postText);
                post.setUserId(userId);
                post.setTopicId(topicId);

                // МНОЖЕСТВЕННЫЕ ФАЙЛЫ
                Collection<Part> parts = req.getParts();
                List<Part> fileParts = parts.stream()
                        .filter(p -> "file".equals(p.getName()) && p.getSize() > 0)
                        .collect(Collectors.toList());

                postService.createPostWithFiles(post, fileParts);

                resp.sendRedirect(req.getContextPath() + "/topic/" + topicId);

            } else if (pathInfo != null && pathInfo.matches("/\\d+/delete")) {
                Long postId = Long.parseLong(pathInfo.split("/")[1]);
                Post post = postService.getPostById(postId);
                User user = (User) req.getSession().getAttribute("user");
                Topic topic = topicService.getTopicById(post.getTopicId());

                if (user == null) {
                    resp.sendError(401);
                    return;
                }

                postService.deleteReactionsByPostId(topic.getId());
                postService.deletePost(postId, user);
                resp.sendRedirect(req.getContextPath() + "/topic/" + topic.getId());

            } else if (pathInfo != null && pathInfo.matches("/\\d+/edit")) {
                Long postId = Long.parseLong(pathInfo.split("/")[1]);
                String newText = req.getParameter("postText");
                Long userId = (Long) req.getSession().getAttribute("userId");

                if (newText != null && !newText.trim().isEmpty()) {
                    postService.updatePost(postId, userId, newText.trim());
                }

                Post post = postService.getPostById(postId);
                resp.sendRedirect(req.getContextPath() + "/topic/" + post.getTopicId());

            } else if (pathInfo != null && pathInfo.matches("/\\d+/reaction")) {
                Long postId = Long.parseLong(pathInfo.split("/")[1]);
                String reaction = req.getParameter("reaction");

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

                postService.toggleReaction(postId, user.getId(), reaction);

                int likes = postService.getReactionCount(postId, "LIKE");
                int dislikes = postService.getReactionCount(postId, "DISLIKE");

                String userReaction = postService.isReaction(postId, user.getId(), "LIKE") ? "LIKE" :
                        postService.isReaction(postId, user.getId(), "DISLIKE") ? "DISLIKE" : null;

                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                String json = String.format("{\"likes\": %d, \"dislikes\": %d, \"reaction\": %s}",
                        likes, dislikes, userReaction == null ? "null" : "\"" + userReaction + "\"");
                resp.getWriter().write(json);

            } else {
                resp.sendError(404);
            }

        } catch (SQLException e) {
            throw new ServletException("Ошибка базы данных", e);
        } catch (NumberFormatException e) {
            resp.sendError(400, "Неверный ID");
        }
    }
}