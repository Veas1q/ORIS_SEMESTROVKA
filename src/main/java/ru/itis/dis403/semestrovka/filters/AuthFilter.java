package ru.itis.dis403.semestrovka.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.itis.dis403.semestrovka.models.User;

import java.io.IOException;

@WebFilter(urlPatterns = {"/topic/create",           // создать топик
        "/topic/*/edit",           // редактировать
        "/topic/*/delete",         // удалить
        "/post/create",            // создать пост
        "/post/*/edit",            // редактировать пост
        "/post/*/delete",          // удалить пост
        "/post/*/like",            // лайк
        "/post/*/report",          // репорт
        "/profile/*",              // профиль
        "/auth/logout"             // выход
})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse  response = (HttpServletResponse) servletResponse;

        HttpSession session = request.getSession(false);

        boolean isLoggedIn = session != null && session.getAttribute("userId") != null;

        if (isLoggedIn) {
            filterChain.doFilter(request, response);
        } else {
            String target = request.getRequestURI();
            if (request.getQueryString() != null) {
                target += "?" + request.getQueryString();
            }

            session = request.getSession(true);
            session.setAttribute("redirectAfterLogin", target);


            response.sendRedirect(request.getContextPath() + "/auth/login");
        }
    }
}