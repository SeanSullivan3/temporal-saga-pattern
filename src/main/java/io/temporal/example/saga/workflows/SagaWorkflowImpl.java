package io.temporal.example.saga.workflows;

import io.temporal.activity.ActivityOptions;
import io.temporal.example.saga.activities.SagaActivities;
import io.temporal.example.saga.pojos.BookingRequest;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;
import io.temporal.example.saga.pojos.*;

import java.time.Duration;

public class SagaWorkflowImpl implements SagaWorkflow {

    private final ActivityOptions options =
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(5))
                    .build();

    private final SagaActivities activities =
            Workflow.newActivityStub(SagaActivities.class, options);

    @Override
    public String rideService(BookingRequest request) {

        Saga saga = new Saga(new Saga.Options.Builder().build());

        // Book a ride
        BookingIdRequestPayload bookingId = activities.createBooking(request);
        saga.addCompensation(activities::cancelBooking, bookingId);
        if (bookingId.getBookingId() == null) {
            saga.compensate();
            return "Booking creation failure";
        }

        // Assign a driver
        CabAssignment cabAssignment = activities.assignDriver(bookingId);
        saga.addCompensation(activities::cancelAssignment, bookingId);
        if (cabAssignment == null) {
            saga.compensate();
            return "Driver assignment failure";
        }

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setBookingId(bookingId.getBookingId());
        paymentRequest.setRiderId(request.getRiderId());

        // Make payment
        Payment payment = activities.makePayment(paymentRequest);
        saga.addCompensation(activities::cancelPayment, payment);
        if (payment.getStatus() == Payment.Status.FAILED) {
            saga.compensate();
            return "Payment failure";
        }

        // Confirm Booking
        Booking confirmedBooking = activities.confirmBooking(bookingId);
        if (confirmedBooking.getStatus() != Booking.Status.CONFIRMED) {
            saga.compensate();
            return "Booking confirmation failure";
        }

        // Notify customer and driver
        Promise<Integer> driver = Async.function(activities::notifyCustomer, confirmedBooking);
        Promise<Integer> rider = Async.function(activities::notifyDriver, confirmedBooking);
        driver.get();
        rider.get();

        return "Successful ride.";
    }
}
