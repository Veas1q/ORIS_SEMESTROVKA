package ru.itis.dis403.semestrovka.listeners;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.itis.dis403.semestrovka.repositories.DBConnection;

@WebListener
public class DBConnectionListener implements ServletContextListener {
    public void contextInitialized(ServletContextEvent event) {
        DBConnection.init();
    }

    public void contextDestroyed(ServletContextEvent sce) {
        DBConnection.destroyConnection();
    }
}
