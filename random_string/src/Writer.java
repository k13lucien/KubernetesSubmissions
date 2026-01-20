import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class Writer {
    public static void main(String[] args) throws IOException {
        String randomString = UUID.randomUUID().toString();
        String filePath = "/usr/src/app/log.txt";  // shared volume path

        System.out.println("Writer started with ID: " + randomString);

        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write("Writer started: " + randomString + "\n");
        }

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String timestamp = Instant.now().toString();
                try (FileWriter writer = new FileWriter(filePath, true)) {
                    writer.write(timestamp + " : " + randomString + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 5000);

        // Keep process alive
        while (true) {
            try { Thread.sleep(60000); } catch (InterruptedException ignored) {}
        }
    }
}
