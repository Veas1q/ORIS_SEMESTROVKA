package ru.itis.dis403.semestrovka.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.dis403.semestrovka.services.CategoryService;
import ru.itis.dis403.semestrovka.services.TopicService;

import java.io.IOException;
import java.sql.SQLException;
@WebServlet(urlPatterns = {"/", "/home"})
public class MainServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("contextPath", req.getContextPath());
            req.setAttribute("categories", categoryService.getAllCategories());
            req.setAttribute("topics", topicService.getRecentTopics(10));
            req.setAttribute("pinnedTopics", topicService.getPinnedTopics());
            req.getRequestDispatcher("/index.ftlh").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
