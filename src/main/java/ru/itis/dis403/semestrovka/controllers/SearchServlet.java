package ru.itis.dis403.semestrovka.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.dis403.semestrovka.models.Topic;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/search")
public class SearchServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String q = req.getParameter("q");
        if (q == null || q.trim().length() < 2) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        q = q.trim();

        try {
            List<Topic> results = topicService.searchTopicsByTitle(q);
            req.setAttribute("query", q);
            req.setAttribute("results", results);
            req.setAttribute("resultCount", results.size());

            req.getRequestDispatcher("/search.ftlh").forward(req, resp);

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска", e);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }
}