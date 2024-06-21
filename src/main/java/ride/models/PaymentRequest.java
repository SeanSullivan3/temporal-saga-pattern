package ride.models;

import lombok.Data;

@Data
public class PaymentRequest {
    private String paymentId;
    private String bookingId;
    private int riderId;
}
