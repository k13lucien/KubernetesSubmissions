package com.todo.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        // Proceed with the request
        filterChain.doFilter(request, response);

        long duration = System.currentTimeMillis() - startTime;

        System.out.printf(
                "Request: %s %s | Status: %d | Duration: %d ms%n",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                duration
        );
    }
}
