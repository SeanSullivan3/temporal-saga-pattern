package io.temporal.example.saga.pojos;

import lombok.Data;

@Data
public class PaymentRequest {
    private String bookingId;
    private int riderId;
}
