package ru.itis.dis403.semestrovka.listeners;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.itis.dis403.semestrovka.repositories.*;
import ru.itis.dis403.semestrovka.services.*;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        DBConnection.init();

        UserRepository userRepo = new UserRepository();
        CategoryRepository categoryRepo = new CategoryRepository();
        TopicRepository topicRepo = new TopicRepository();
        PostRepository postRepo = new PostRepository();

        UserService userService = new UserService(userRepo);
        CategoryService categoryService = new CategoryService(categoryRepo);
        TopicService topicService = new TopicService(topicRepo);
        PostService postService = new PostService(postRepo);

        var ctx = event.getServletContext();
        ctx.setAttribute("userService", userService);
        ctx.setAttribute("categoryService", categoryService);
        ctx.setAttribute("topicService", topicService);
        ctx.setAttribute("postService", postService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DBConnection.destroyConnection();
    }
}