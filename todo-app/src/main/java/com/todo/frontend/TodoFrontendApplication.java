package com.todo.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.todo.frontend")
public class TodoFrontendApplication {
    public static void main(String[] args) {
        SpringApplication.run(TodoFrontendApplication.class, args);
    }
}
