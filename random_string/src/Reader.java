import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

public class Reader {
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/status", new LogHandler());
        server.start();
        System.out.println("Reader started on port " + port);
    }

    static class LogHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Path logPath = Path.of("/usr/src/app/log.txt");
            Path pingPath = Path.of("/usr/src/app/pingpong.txt");

            String logContent = Files.exists(logPath)
                    ? Files.readString(logPath).trim()
                    : "Log file not found.";

            String pingCount = Files.exists(pingPath)
                    ? Files.readString(pingPath).trim()
                    : "0";

            String response = logContent + "\nPing / Pongs: " + pingCount + "\n";

            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
