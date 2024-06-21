package ride.models;

import lombok.Data;

@Data
public class BookingRequest {
    private String BookingId;
    private int riderId;
    private String pickUpLocation;
    private String dropOffLocation;
}
