import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;

public class Reader {

    private static final String PINGPONG_URL = "http://pingpong-svc:2345/pings";
    private static final String RANDOM_ID = UUID.randomUUID().toString();

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new LogHandler());
        server.createContext("/healthz", new HealthHandler());
        server.start();
        System.out.println("Health server started on port " + port);
        System.out.println("Reader started on port " + port);
    }

    static class LogHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Read configmap data
            String message = System.getenv("MESSAGE");
            Path infoPath = Path.of("/usr/src/app/config/information.txt");
            String fileContent = Files.exists(infoPath)
                    ? Files.readString(infoPath).trim()
                    : "File not found";

            // Fetch ping count
            String pingCount = fetchPingCount();

            // Generate timestamp
            String timestamp = Instant.now().toString();

            // Build response
            String response = String.format("""
                    file content: %s
                    env variable: MESSAGE=%s
                    %s: %s
                    Ping / Pongs: %s
                    """, fileContent, message, timestamp, RANDOM_ID, pingCount);

            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        private String fetchPingCount() {
            try {
                URL url = new URL(PINGPONG_URL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    return in.readLine().trim();
                }
            } catch (Exception e) {
                return "N/A (PingPong service unreachable)";
            }
        }
    }

    static class HealthHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String pingpongUrl = System.getenv().getOrDefault("PINGPONG_URL", "http://pingpong-svc:2345/pings");
        String response = "ok";
        int status = 200;

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(pingpongUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.getResponseCode();
        } catch (IOException e) {
            status = 500;
            response = "cannot reach pingpong: " + e.getMessage();
        }

        exchange.sendResponseHeaders(status, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}

}
