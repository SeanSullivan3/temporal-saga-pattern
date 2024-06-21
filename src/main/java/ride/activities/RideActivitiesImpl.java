package ride.activities;

import io.temporal.activity.Activity;
import io.temporal.spring.boot.ActivityImpl;
import org.springframework.stereotype.Component;
import ride.models.*;
import ride.dao.ActivitiesDAO;

import java.sql.SQLException;
import java.util.Random;

@Component
@ActivityImpl(workers = "saga-pattern-worker")
public class RideActivitiesImpl implements RideActivities {

    private static final ActivitiesDAO db = new ActivitiesDAO("jdbc:sqlite:cab_saga.db");

    @Override
    public BookingId createBooking(BookingRequest request) {

        Booking booking = new Booking();
        booking.setBookingId(request.getBookingId());
        booking.setRiderId(request.getRiderId());
        booking.setPickUpLocation(request.getPickUpLocation());
        booking.setDropOffLocation(request.getDropOffLocation());
        booking.setStatus(Booking.Status.PENDING);

        try {
            db.insertBooking(booking);
        }
        catch (SQLException e) {
            throw Activity.wrap(e);
        }

        BookingId id = new BookingId();
        id.setBookingId(booking.getBookingId());
        return id;
    }

    @Override
    public void cancelBooking(BookingId id) {
        try {
            Booking booking = new Booking();
            db.readBooking(id.getBookingId(), booking);
            booking.setStatus(Booking.Status.CANCELLED);
            booking.setDriverId(0);
            db.updateBooking(booking);
        }
        catch (SQLException ignored) {}
    }

    @Override
    public PaymentRequest assignDriver(BookingId id) {

        Random rand = new Random();
        int driverId = rand.nextInt(4) + 1;
        CabAssignment cabAssignment = new CabAssignment();
        cabAssignment.setBookingId(id.getBookingId());
        cabAssignment.setDriverId(driverId);
        cabAssignment.setActive(true);

        Booking booking = new Booking();
        try {
            db.insertAssignment(cabAssignment);

            db.readBooking(id.getBookingId(), booking);
            booking.setDriverId(driverId);
            booking.setStatus(Booking.Status.ASSIGNED);
            db.updateBooking(booking);
        }
        catch (SQLException e) {
            throw Activity.wrap(e);
        }

        PaymentRequest request = new PaymentRequest();
        request.setBookingId(id.getBookingId());
        request.setRiderId(booking.getRiderId());
        return request;
    }

    @Override
    public void cancelAssignment(BookingId id) {
        try {
            db.deactivateAssignment(id.getBookingId());
        }
        catch (SQLException ignored) {}
    }

    @Override
    public Payment makePayment(PaymentRequest request) {

        PaymentMethod paymentMethod = new PaymentMethod();
        Payment payment = new Payment();
        payment.setPaymentId(request.getPaymentId());
        payment.setBookingId(request.getBookingId());
        payment.setAmount(40.0);
        payment.setPaymentMethodId(0);
        payment.setStatus(Payment.Status.PENDING);

        try {
            db.readPaymentMethod(request.getRiderId(), paymentMethod);
            db.insertPayment(payment);
        }
        catch (SQLException e) {
            payment.setErrorMsg("Payment creation failure");
            payment.setStatus(Payment.Status.FAILED);
            throw Activity.wrap(e);
        }

        try {
            if (paymentMethod.getId() > 0) {
                payment.setPaymentMethodId(paymentMethod.getId());
                //Some external api call
                //Assume payment went through
                payment.setStatus(Payment.Status.SUCCESSFUL);
            }
            else {
                payment.setErrorMsg("Rider doesn't have a payment method added");
                payment.setStatus(Payment.Status.FAILED);
            }
            db.updatePayment(payment);
        }
        catch (SQLException e) {
            throw Activity.wrap(e);
        }
        return payment;
    }

    @Override
    public void cancelPayment(PaymentRequest request) {
        //External api call to reverse any payment
    }

    @Override
    public void notify(BookingId id) {

        Booking booking = new Booking();
        try {
            db.readBooking(id.getBookingId(), booking);
            booking.setStatus(Booking.Status.CONFIRMED);
            db.updateBooking(booking);
        }
        catch (SQLException e) {
            throw Activity.wrap(e);
        }

        DriverNotificationRequest driverNotif = new DriverNotificationRequest();
        driverNotif.setBookingId(id.getBookingId());
        driverNotif.setDriverId(booking.getDriverId());
        driverNotif.setPickUp(booking.getPickUpLocation());
        driverNotif.setDropOff(booking.getDropOffLocation());
        //Notify driver through some api

        //Notify rider through some api
    }
}
