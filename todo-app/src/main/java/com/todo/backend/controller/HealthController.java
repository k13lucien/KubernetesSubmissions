package com.todo.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/healthz")
    public String healthz() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(1)) {
                return "ok";
            } else {
                return "db not valid";
            }
        } catch (Exception e) {
            return "db not reachable: " + e.getMessage();
        }
    }
}
