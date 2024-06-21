package ride.models;

import lombok.Data;

@Data
public class PaymentMethod {
    private int id;
    private int riderId;
    private String details;
}
