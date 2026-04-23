package A;

public class EmailNotification extends BaseNotification{
    private String email;

    public EmailNotification(String email) {
        super();
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "A.EmailNotification{" +
                "email='" + email + '\'' +
                '}';
    }

    @Override
    public void send(String message) {
        log(message);
        System.out.println("Trimitere EMAIL: " + message);
    }

    @Override
    public String getChannel() {
        return "EMAIL";
    }
}
