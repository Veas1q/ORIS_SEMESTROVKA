package ru.itis.dis403.semestrovka.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.dis403.semestrovka.models.User;
import ru.itis.dis403.semestrovka.services.UserService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

@WebFilter("/*")
public class BanFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        req.setAttribute("contextPath", request.getContextPath());

        UserService userService = (UserService) request.getServletContext()
                .getAttribute("userService");

        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId != null ) {
            try {
                User user = userService.findById(userId);
                if (user != null && user.getIsBanned()) {
                    if (user.getBannedUntil() != null && user.getBannedUntil().isBefore(LocalDateTime.now())) {
                        userService.unbanUser(userId);
                    } else {
                        request.setAttribute("banReason", user.getBanReason());
                        request.setAttribute("bannedUntil", user.getBannedUntil());
                        request.getRequestDispatcher("/banned.ftlh").forward(request, response);
                        return;
                    }
                }
            } catch (SQLException e) {
                throw new ServletException("Ошибка при проверке бана пользователя", e);
            }
        }

        chain.doFilter(req, resp);
    }
}