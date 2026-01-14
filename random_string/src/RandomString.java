import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class RandomString {

    public static void main(String[] args) {
        // Generate a random UUID and store it in memory
        String randomString = UUID.randomUUID().toString();

        System.out.println("Application started with ID: " + randomString);

        // Create a timer to print the UUID every 5 seconds
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String timestamp = Instant.now().toString();
                System.out.println(timestamp + ": " + randomString);
            }
        }, 0, 5000); // 0 delay, repeat every 5000 ms (5 seconds)
    }
}
