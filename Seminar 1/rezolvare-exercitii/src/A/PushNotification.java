package A;

public class PushNotification extends BaseNotification {
    private String deviceToken;

    public PushNotification(String deviceToken) {
        super();
        this.deviceToken = deviceToken;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    @Override
    public String toString() {
        return "A.PushNotification{" +
                "deviceToken='" + deviceToken + '\'' +
                '}';
    }

    @Override
    public void send(String message) {
        log(message);
        System.out.println("Trimitere PUSH: " + message);
    }

    @Override
    public String getChannel() {
        return "PUSH";
    }
}
