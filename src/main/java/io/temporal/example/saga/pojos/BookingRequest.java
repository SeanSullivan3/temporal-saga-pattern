package io.temporal.example.saga.pojos;

import lombok.Data;

@Data
public class BookingRequest {
    private String BookingId;
    private int riderId;
    private String pickUpLocation;
    private String dropOffLocation;
}
