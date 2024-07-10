package io.temporal.example.saga.activities;

import io.temporal.example.saga.pojos.*;
import io.temporal.example.saga.service.BookingService;
import io.temporal.example.saga.service.CabAssignmentService;
import io.temporal.example.saga.service.PaymentService;

public class SagaActivitiesImpl implements SagaActivities {


    @Override
    public BookingIdRequestPayload createBooking(BookingRequest request) {
        String bookingId = BookingService.createBooking(request);
        BookingIdRequestPayload result = new BookingIdRequestPayload();
        result.setBookingId(bookingId);
        return result;
    }

    @Override
    public void cancelBooking(BookingIdRequestPayload cancelBookingRequest) {
        Booking booking = BookingService.getBooking(cancelBookingRequest.getBookingId());
        BookingService.cancelBooking(booking);
    }

    @Override
    public CabAssignment assignDriver(BookingIdRequestPayload id) {
        return CabAssignmentService.assignDriver(id.getBookingId());
    }

    @Override
    public void cancelAssignment(BookingIdRequestPayload driverCancellationRequest) {
        CabAssignmentService.cancelAssignment(driverCancellationRequest.getBookingId());
    }

    @Override
    public Payment makePayment(PaymentRequest request) {
        return PaymentService.createPayment(request);
    }

    @Override
    public void cancelPayment(Payment payment) {
        PaymentService.cancelPayment(payment);
    }

    @Override
    public Booking confirmBooking(BookingIdRequestPayload bookingConfirmationReq) {
        Booking booking = BookingService.getBooking(bookingConfirmationReq.getBookingId());
        BookingService.confirmBooking(booking);
        return booking;
    }

    @Override
    public void notifyDriver(Booking booking) {

    }

    @Override
    public void notifyCustomer(Booking booking) {

    }
}
