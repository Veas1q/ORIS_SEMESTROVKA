package ru.itis.dis403.semestrovka.controllers;

import jakarta.servlet.http.HttpServlet;
import ru.itis.dis403.semestrovka.services.CategoryService;
import ru.itis.dis403.semestrovka.services.PostService;
import ru.itis.dis403.semestrovka.services.TopicService;
import ru.itis.dis403.semestrovka.services.UserService;

public abstract class BaseServlet extends HttpServlet {

    protected UserService userService;
    protected CategoryService categoryService;
    protected TopicService topicService;
    protected PostService postService;

    public void init() {
        var servletContext = getServletContext();
        categoryService = (CategoryService) servletContext.getAttribute("categoryService");
        postService = (PostService) servletContext.getAttribute("postService");
        topicService = (TopicService) servletContext.getAttribute("topicService");
        userService = (UserService) servletContext.getAttribute("userService");
    }
}
