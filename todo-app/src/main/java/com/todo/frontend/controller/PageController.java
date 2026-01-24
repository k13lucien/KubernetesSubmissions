package com.todo.frontend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

@Controller
public class PageController {

    private static final Path CACHE_DIR = Paths.get("/usr/src/app/cache");
    private static final Path IMAGE_FILE = CACHE_DIR.resolve("image.jpg");
    private static final Path META_FILE = CACHE_DIR.resolve("timestamp.txt");

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${BACKEND_URL}")
    private String backendUrl;

    @Value("${IMAGE_URL:https://picsum.photos/1200}")
    private String imageUrl;

    @Value("${CACHE_DURATION:600000}") // default 10 min
    private long cacheDuration;

    public PageController() throws IOException {
        Files.createDirectories(CACHE_DIR);
    }

    @GetMapping("/")
    public String getPage(Model model) throws IOException {
        ensureCachedImage();

        List<Map<String, Object>> todos = List.of();
        try {
            List<Map<String, Object>> response = restTemplate.getForObject(backendUrl, List.class);
            if (response != null) todos = response;
        } catch (Exception e) {
            System.err.println("Failed to fetch todos: " + e.getMessage());
        }

        model.addAttribute("todos", todos);
        return "index";
    }

    @PostMapping("/add-todo")
    public String addTodo(@RequestParam String text) {
        if (text != null && !text.isBlank() && text.length() <= 140) {
            try {
                restTemplate.postForObject(backendUrl, Map.of("text", text), String.class);
            } catch (Exception e) {
                System.err.println("Failed to add todo: " + e.getMessage());
            }
        }
        return "redirect:/";
    }

    @GetMapping(value = "/image", produces = "image/jpeg")
    @ResponseBody
    public byte[] getImage() throws IOException {
        ensureCachedImage();
        return Files.readAllBytes(IMAGE_FILE);
    }

    private void ensureCachedImage() throws IOException {
        boolean needNew = true;
        if (Files.exists(IMAGE_FILE) && Files.exists(META_FILE)) {
            long timestamp = Long.parseLong(Files.readString(META_FILE));
            if (System.currentTimeMillis() - timestamp < cacheDuration) {
                needNew = false;
            }
        }
        if (needNew) downloadImage();
    }

    private void downloadImage() throws IOException {
        URL url = new URL(imageUrl);
        try (InputStream in = url.openStream()) {
            Files.copy(in, IMAGE_FILE, StandardCopyOption.REPLACE_EXISTING);
        }
        Files.writeString(META_FILE,
                String.valueOf(System.currentTimeMillis()),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }
}
