package ru.itis.dis403.semestrovka.controllers;

import jakarta.servlet.http.HttpServlet;
import ru.itis.dis403.semestrovka.services.*;

public abstract class BaseServlet extends HttpServlet {

    protected UserService userService;
    protected CategoryService categoryService;
    protected TopicService topicService;
    protected PostService postService;
    protected AttachmentService attachmentService;

    public void init() {
        var servletContext = getServletContext();
        categoryService = (CategoryService) servletContext.getAttribute("categoryService");
        postService = (PostService) servletContext.getAttribute("postService");
        topicService = (TopicService) servletContext.getAttribute("topicService");
        userService = (UserService) servletContext.getAttribute("userService");
        attachmentService = (AttachmentService) servletContext.getAttribute("attachmentService");
    }
}
