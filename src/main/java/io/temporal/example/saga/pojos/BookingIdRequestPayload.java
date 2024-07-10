package io.temporal.example.saga.pojos;

import lombok.Data;

@Data
public class BookingIdRequestPayload {
    private String bookingId;
}
