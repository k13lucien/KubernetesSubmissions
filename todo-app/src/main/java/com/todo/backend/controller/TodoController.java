package com.todo.backend.controller;

import com.todo.backend.model.Todo;
import com.todo.backend.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class TodoController {

    private final TodoRepository repository;

    @Autowired
    public TodoController(TodoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/todos")
    public List<Todo> getTodos() {
        return repository.findAll();
    }

    @PostMapping("/todos")
    public Todo create(@RequestBody Todo todo) {
        return repository.save(todo);
    }
}
