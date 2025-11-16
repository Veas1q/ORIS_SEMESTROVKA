package ru.itis.dis403.semestrovka.listeners;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.itis.dis403.semestrovka.repositories.DBConnection;
import ru.itis.dis403.semestrovka.services.*;

@WebListener
public class DBConnectionListener implements ServletContextListener {
    public void contextInitialized(ServletContextEvent event) {
        DBConnection.init();

        CategoryService categoryService = new CategoryService();
        UserService userService = new UserService();
        PostService postService = new PostService();
        TopicService topicService = new TopicService();
        AttachmentService attachmentService = new AttachmentService();

        var  ctx = event.getServletContext();
        ctx.setAttribute("categoryService", categoryService);
        ctx.setAttribute("userService", userService);
        ctx.setAttribute("postService", postService);
        ctx.setAttribute("topicService", topicService);
        ctx.setAttribute("attachmentService", attachmentService);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        DBConnection.destroyConnection();
    }
}
