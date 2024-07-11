package io.temporal.example.saga.service;

import io.temporal.example.saga.dao.BookingDAO;
import io.temporal.example.saga.pojos.Booking;
import io.temporal.example.saga.pojos.BookingRequest;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class BookingService {

    private static final BookingDAO bookingDAO = new BookingDAO("jdbc:sqlite:cab_saga.db");

    public static String createBooking(BookingRequest bookingRequest) {
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();

        Booking booking = new Booking();
        booking.setBookingId(uuidAsString);
        booking.setRiderId(bookingRequest.getRiderId());
        booking.setPickUpLocation(bookingRequest.getPickUpLocation());
        booking.setDropOffLocation(bookingRequest.getDropOffLocation());
        booking.setStatus(Booking.Status.PENDING);

        String error = bookingDAO.insertBooking(booking);

        if (error.isEmpty()) {
            log.info("Created booking with id: {}", booking.getBookingId());
        }
        else {
            log.error("Booking creation failure: {}", error);
            return null;
        }

        return uuidAsString;
    }

    public static Booking getBooking(String bookingId) {
        Booking booking = new Booking();
        bookingDAO.readBooking(bookingId, booking);
        return booking;
    }

    public static boolean assignDriverToBooking(Booking booking, int driverId) {
        booking.setDriverId(driverId);
        booking.setStatus(Booking.Status.ASSIGNED);
        return bookingDAO.updateBooking(booking);
    }

    public static void confirmBooking(Booking booking) {
        booking.setStatus(Booking.Status.CONFIRMED);
        if (bookingDAO.updateBooking(booking)) {
            log.info("Confirmed booking with id: {}", booking.getBookingId());
        }
        else {
            log.error("Booking confirmation failure");
        }
    }

    public static boolean cancelBooking(Booking booking) {
        booking.setStatus(Booking.Status.CANCELLED);
        booking.setDriverId(0);
        log.error("Cancelling booking with id: {}", booking.getBookingId());
        return bookingDAO.updateBooking(booking);
    }
}
