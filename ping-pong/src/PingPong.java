import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.*;

public class PingPong {

    private static Connection connection;

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        String dbUrl = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/pingpong");
        String dbUser = System.getenv().getOrDefault("DB_USER", "ppuser");
        String dbPassword = System.getenv().getOrDefault("DB_PASSWORD", "pppassword");

        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("✅ Connected to PostgreSQL database");

            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ping_counter (id SERIAL PRIMARY KEY, count INT)");
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM ping_counter");
                rs.next();
                if (rs.getInt(1) == 0) {
                    stmt.executeUpdate("INSERT INTO ping_counter (count) VALUES (0)");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("❌ Failed to connect to database: " + e.getMessage(), e);
        }

        // Create and start the server
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/pingpong", new PingHandler());
        server.createContext("/pings", new CountHandler());
        server.createContext("/healthz", new HealthHandler()); // 👈 Added readiness endpoint
        server.setExecutor(null);
        server.start();
        System.out.println("Ping-Pong server started on port " + port);
    }

    static class PingHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            int count = 0;
            try (PreparedStatement ps = connection.prepareStatement("UPDATE ping_counter SET count = count + 1 RETURNING count")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) count = rs.getInt("count");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            String response = "pong " + count;
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    static class CountHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            int count = 0;
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT count FROM ping_counter LIMIT 1")) {
                if (rs.next()) count = rs.getInt("count");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            String response = String.valueOf(count);
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    // ✅ NEW: Health check endpoint for readiness probe
    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String response = "ok";
            int status = 200;

            try (Statement stmt = connection.createStatement()) {
                stmt.executeQuery("SELECT 1");
            } catch (SQLException e) {
                status = 500;
                response = "db not ready: " + e.getMessage();
            }

            exchange.sendResponseHeaders(status, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
