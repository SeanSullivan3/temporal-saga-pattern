package ride.models;

import lombok.Data;

@Data
public class CabAssignment {
    private int id;
    private String bookingId;
    private int driverId;
    private long createdAt;
    private Boolean active;
}
