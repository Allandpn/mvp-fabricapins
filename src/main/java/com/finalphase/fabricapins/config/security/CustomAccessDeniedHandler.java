package com.finalphase.fabricapins.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalphase.fabricapins.exception.model.CustomError;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Autowired
    private ObjectMapper mapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        HttpStatus status = HttpStatus.FORBIDDEN;
        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                accessDeniedException.getMessage(),
                request.getRequestURI()
        );
        response.setStatus(status.value());
        response.setContentType("application/json");

        response.getWriter().write(mapper.writeValueAsString(err));
    }
}
