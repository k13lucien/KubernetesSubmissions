package com.todo.backend.controller;

import com.todo.backend.model.Todo;
import com.todo.backend.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> addTodo(@RequestBody Todo todo) {
        if (todo.getText() == null || todo.getText().length() > 140) {
            System.out.println("Rejected todo: too long or empty -> " + todo.getText());
            return ResponseEntity
                    .badRequest()
                    .body("Todo text must not exceed 140 characters.");
        }

        Todo savedTodo = repository.save(todo);
        System.out.println("New todo added: " + todo.getText());
        return ResponseEntity.ok(savedTodo);
    }

}
