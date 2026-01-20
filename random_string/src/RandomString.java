import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class RandomString {

    private static String randomString;
    private static String timestamp;

    public static void main(String[] args) throws IOException {
        // 1️⃣ Generate random UUID
        randomString = UUID.randomUUID().toString();
        timestamp = Instant.now().toString();

        System.out.println("Application started with ID: " + randomString);

        // 2️⃣ Start the background logger
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timestamp = Instant.now().toString();
                System.out.println(timestamp + ": " + randomString);
            }
        }, 0, 5000);

        // 3️⃣ Start a lightweight web server
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/status", new StatusHandler());
        server.setExecutor(null);
        server.start();

        System.out.println("Server started in port " + port);
    }

    // 4️⃣ HTTP handler for /status
    static class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = String.format("{\"timestamp\":\"%s\", \"randomString\":\"%s\"}", timestamp, randomString);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
