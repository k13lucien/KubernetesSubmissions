package com.todo.backend.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class TodoController {

    private final List<Map<String, Object>> todos = new ArrayList<>();

    @GetMapping("/todos")
    public List<Map<String, Object>> getTodos() {
        return todos;
    }

    @PostMapping("/todos")
    public Map<String, Object> create(@RequestBody Map<String, Object> body) {
        String text = (String) body.get("text");
        if (text != null && text.length() <= 140) {
            todos.add(Map.of("text", text));
        }
        return body;
    }
}
