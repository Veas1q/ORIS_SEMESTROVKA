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

@WebServlet("/post/*")
public class PostServlet extends HttpServlet {
    private PostService postService = new PostService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            String pathInfo = req.getPathInfo();
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

    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo.equals("/create")) {
                Post post = new Post();
                post.setPostText(req.getParameter("postText"));
                post.setUserId(Long.parseLong(req.getParameter("userId")));
                post.setTopicId(Long.parseLong(req.getParameter("topicId")));
                req.getRequestDispatcher("create-post.ftlh").forward(req, resp);
            }  else if (pathInfo.matches("/\\d+/delete")) {
                Long postId = Long.parseLong(pathInfo.split("/")[1]);
                User user = (User) req.getSession().getAttribute("user");
                postService.deletePost(postId, user);

                req.getRequestDispatcher("delete-post.ftlh").forward(req, resp);
            } else if (pathInfo.matches("/\\d+/edit")) {
                Long postId = Long.parseLong(pathInfo.split("/")[1]);

                Long userId = (Long) req.getSession().getAttribute("userId");

                postService.updatePost(postId, userId);
                req.getRequestDispatcher("edit-post.ftlh").forward(req, resp);
            } else {
                resp.sendError(404);
            }
        } catch (NumberFormatException | IOException | ServletException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
