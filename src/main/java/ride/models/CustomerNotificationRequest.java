package ride.models;

import lombok.Data;

@Data
public class CustomerNotificationRequest {
    int riderId;
    String dropOff;
    String pickUp;
    String bookingId;
}
