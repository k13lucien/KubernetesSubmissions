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
        // 1️⃣ Check if we need a new image
        boolean needNew = true;
        if (Files.exists(IMAGE_FILE) && Files.exists(META_FILE)) {
            long timestamp = Long.parseLong(Files.readString(META_FILE));
            if (System.currentTimeMillis() - timestamp < CACHE_DURATION) {
                needNew = false;
            }
        }

        if (needNew) {
            downloadImage();
        }

        // 2️⃣ Return HTML referencing the image endpoint
        String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>The Project App</title>
                </head>
                <body style="text-align:center; font-family: Arial, sans-serif;">
                    <h1>The Project App</h1>
                    <img src="/image" alt="Random Image" style="max-width:90%%; height:auto; margin:20px 0;">
                    <p>Devops with Kubernetes</p>
                </body>
                </html>
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        return new ResponseEntity<>(html, headers, HttpStatus.OK);
    }

    @GetMapping("/image")
    public ResponseEntity<byte[]> getImage() throws IOException {
        byte[] imageBytes = Files.readAllBytes(IMAGE_FILE);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    private void downloadImage() throws IOException {
        URL url = new URL("https://picsum.photos/1200");
        try (InputStream in = url.openStream()) {
            Files.copy(in, IMAGE_FILE, StandardCopyOption.REPLACE_EXISTING);
        }
        Files.writeString(META_FILE, String.valueOf(System.currentTimeMillis()), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
