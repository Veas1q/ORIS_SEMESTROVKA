package ru.itis.dis403.semestrovka.listeners;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.itis.dis403.semestrovka.repositories.*;
import ru.itis.dis403.semestrovka.services.*;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        // Инициализируем пул соединений HikariC
        DBConnection.init();

        UserRepository userRepo = new UserRepository();
        CategoryRepository categoryRepo = new CategoryRepository();
        TopicRepository topicRepo = new TopicRepository();
        PostRepository postRepo = new PostRepository();

        UserService userService = new UserService(userRepo);
        CategoryService categoryService = new CategoryService(categoryRepo);
        TopicService topicService = new TopicService(topicRepo);
        PostService postService = new PostService(postRepo);

        // Получаем сервлет контекст, который живёт всё время работы приложения и кладем в него сервисы
        ServletContext servletContext = event.getServletContext();
        servletContext.setAttribute("userService", userService);
        servletContext.setAttribute("categoryService", categoryService);
        servletContext.setAttribute("topicService", topicService);
        servletContext.setAttribute("postService", postService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DBConnection.destroyConnection();
    }
}