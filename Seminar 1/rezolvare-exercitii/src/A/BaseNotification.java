package A;

import java.time.LocalDateTime;

public abstract class BaseNotification implements Notifiable {
    private final LocalDateTime timestamp;

    public BaseNotification() {
        this.timestamp = LocalDateTime.now();
    }

    public void log(String message){
        System.out.println("[" + this.getChannel() + "] [" + this.timestamp + "] " + message);
    }
}
