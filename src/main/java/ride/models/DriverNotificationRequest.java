package ride.models;

import lombok.Data;

@Data
public class DriverNotificationRequest {
    int driverId;
    String dropOff;
    String pickUp;
    String bookingId;
}
