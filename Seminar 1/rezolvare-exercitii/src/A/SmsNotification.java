package A;

public class SmsNotification extends BaseNotification{
    private String phoneNo;

    public SmsNotification(String phoneNo) {
        super();
        this.phoneNo = phoneNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    @Override
    public String toString() {
        return "A.SmsNotification{" +
                "phoneNo='" + phoneNo + '\'' +
                '}';
    }

    @Override
    public void send(String message) {
        log(message);
        System.out.println("Trimitere SMS: " + message);
    }

    @Override
    public String getChannel() {
        return "SMS";
    }
}
