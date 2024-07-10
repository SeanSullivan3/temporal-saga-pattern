package io.temporal.example.saga.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import io.temporal.example.saga.pojos.*;

@ActivityInterface
public interface SagaActivities {

    @ActivityMethod
    public BookingIdRequestPayload createBooking(BookingRequest request);

    @ActivityMethod
    public void cancelBooking(BookingIdRequestPayload cancelBookingRequest);

    @ActivityMethod
    public CabAssignment assignDriver(BookingIdRequestPayload id);

    @ActivityMethod
    public void cancelAssignment(BookingIdRequestPayload driverCancellationRequest);

    @ActivityMethod
    public Payment makePayment(PaymentRequest request);

    @ActivityMethod
    public void cancelPayment(Payment payment);

    @ActivityMethod
    public Booking confirmBooking(BookingIdRequestPayload bookingId);

    @ActivityMethod
    public void notifyDriver(Booking booking);

    @ActivityMethod
    public void notifyCustomer(Booking booking);
}
