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

@WebServlet("/category/*")
public class CategoryServlet extends HttpServlet {
    private CategoryService categoryService = new CategoryService();
    private TopicService topicService = new TopicService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                req.setAttribute("categories", categoryService.getAllCategories());
                req.getRequestDispatcher("/categories.ftlh").forward(req, resp);
            } else {
                Long categoryId = Long.parseLong(pathInfo.substring(1));
                req.setAttribute("category", categoryService.getCategoryById(categoryId));
                req.setAttribute("topics", topicService.getTopicsByCategoryId(categoryId));
                req.getRequestDispatcher("/category.ftlh").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        } catch (NumberFormatException e) {
            resp.sendError(404, "Category not found");
        }
    }
}