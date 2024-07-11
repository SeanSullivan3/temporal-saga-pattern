package io.temporal.example.saga.service;

import io.temporal.example.saga.dao.CabAssignmentDAO;
import io.temporal.example.saga.pojos.Booking;
import io.temporal.example.saga.pojos.CabAssignment;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class CabAssignmentService {

    private static final CabAssignmentDAO cabAssignmentDAO = new CabAssignmentDAO("jdbc:sqlite:cab_saga.db");

    public static CabAssignment assignDriver(String bookingId) {
        int driverId = findDriver();

        Booking booking = BookingService.getBooking(bookingId);

        if (booking.getBookingId().isEmpty()) {
            log.error("Booking with id {} not found.", bookingId);
            return null;
        }

        CabAssignment cabAssignment = new CabAssignment();
        cabAssignment.setBookingId(bookingId);
        cabAssignment.setDriverId(driverId);
        cabAssignment.setActive(true);

        if (!cabAssignmentDAO.insertAssignment(cabAssignment)) {
            log.error("Cab assignment failed for booking with id: {}", bookingId);
            return null;
        }

        BookingService.assignDriverToBooking(booking, driverId);

        log.info("Assigned driver {} to booking with id: {}", driverId, bookingId);

        return cabAssignment;
    }

    public static void cancelAssignment(String bookingId) {
        Booking booking = BookingService.getBooking(bookingId);
        log.error("Cancelling driver assignment for booking with id: {}", bookingId);

        if (booking.getBookingId().isEmpty()) {
            log.error("Booking with id: {} not found.", bookingId);
        }

        cabAssignmentDAO.deactivateAssignment(bookingId);
    }

    private static int findDriver() {
        Random random = new Random();
        int driverId = 0;
        while (true) {
            driverId = random.nextInt(5);
            if(driverId !=0) break;
        }
        return driverId;
    }
}
