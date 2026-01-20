package com.todo;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import java.util.List;

@RestController
public class PageController {

    private static final Path CACHE_DIR = Paths.get("/usr/src/app/cache");
    private static final Path IMAGE_FILE = CACHE_DIR.resolve("image.jpg");
    private static final Path META_FILE = CACHE_DIR.resolve("timestamp.txt");
    private static final long CACHE_DURATION = 10 * 60 * 1000; // 10 minutes

    public PageController() throws IOException {
        Files.createDirectories(CACHE_DIR);
    }

    @GetMapping("/")
    public ResponseEntity<String> getPage() throws IOException {
        // 1️⃣ Ensure image is cached
        boolean needNew = true;
        if (Files.exists(IMAGE_FILE) && Files.exists(META_FILE)) {
            long timestamp = Long.parseLong(Files.readString(META_FILE));
            if (System.currentTimeMillis() - timestamp < CACHE_DURATION) {
                needNew = false;
            }
        }
        if (needNew) downloadImage();

        // 2️⃣ Hardcoded todos
        List<String> todos = List.of(
                "Finish Kubernetes practice",
                "Push Docker image to registry",
                "Write blog post about Spring Boot"
        );

        // 3️⃣ Build HTML
        String html = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <title>The Project App</title>
            <style>
                body { text-align:center; font-family: Arial, sans-serif; }
                #todo-list { list-style: none; padding: 0; }
                #todo-list li { margin: 5px 0; }
                input[type=text] { width: 300px; padding: 5px; }
                button { padding: 5px 10px; }
            </style>
            <script>
                function addTodo() {
                    const input = document.getElementById('todo-input');
                    if (input.value.length === 0) return alert('Please enter a todo!');
                    if (input.value.length > 140) return alert('Todo cannot exceed 140 characters!');
                    const li = document.createElement('li');
                    li.textContent = input.value;
                    document.getElementById('todo-list').appendChild(li);
                    input.value = '';
                }
            </script>
        </head>
        <body>
            <h1>The Project App</h1>
            <img src="/image" alt="Random Image" style="max-width:90%; height:auto; margin:20px 0;">
            
            <h2>Todo List</h2>
            <input type="text" id="todo-input" placeholder="Enter a new todo (max 140 chars)">
            <button onclick="addTodo()">Add Todo</button>
            
            <ul id="todo-list">
    """;

        // 4️⃣ Add hardcoded todos
        for (String todo : todos) {
            html += "<li>" + todo + "</li>\n";
        }

        html += """
            </ul>
            <p>Devops with Kubernetes</p>
        </body>
        </html>
    """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        return new ResponseEntity<>(html, headers, HttpStatus.OK);
    }


    private void downloadImage() throws IOException {
        URL url = new URL("https://picsum.photos/1200");
        try (InputStream in = url.openStream()) {
            Files.copy(in, IMAGE_FILE, StandardCopyOption.REPLACE_EXISTING);
        }
        Files.writeString(META_FILE, String.valueOf(System.currentTimeMillis()), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
