package ride.activities;

import io.temporal.activity.Activity;
import io.temporal.failure.ApplicationFailure;
import io.temporal.failure.TemporalFailure;
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
        System.out.println("Booking created.");
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
            System.out.println("Canceled booking.");
        }
        catch (SQLException ignored) {}
    }

    @Override
    public PaymentRequest assignDriver(BookingId id) {

        Random rand = new Random();
        int driverId = rand.nextInt(3) + 1;
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
        System.out.println("Driver " + driverId + " assigned to booking.");
        return request;
    }

    @Override
    public void cancelAssignment(BookingId id) {
        try {
            db.deactivateAssignment(id.getBookingId());
            System.out.println("Canceled driver assignment.");
        }
        catch (SQLException ignored) {}
    }

    @Override
    public Payment makePayment(PaymentRequest request) {

        Payment payment = new Payment();
        payment.setPaymentId(request.getPaymentId());
        payment.setBookingId(request.getBookingId());
        payment.setAmount(40.0);
        payment.setPaymentMethodId(0);
        payment.setStatus(Payment.Status.PENDING);
        try {
            db.insertPayment(payment);
        }
        catch (SQLException e) {
            payment.setErrorMsg("Failed to create payment.");
            payment.setStatus(Payment.Status.FAILED);
            return payment;
        }

        PaymentMethod paymentMethod = new PaymentMethod();
        try {
            db.readPaymentMethod(request.getRiderId(), paymentMethod);
            payment.setPaymentMethodId(paymentMethod.getId());
        }
        catch (SQLException e) {
            payment.setErrorMsg("Customer doesn't have a payment method added.");
            payment.setStatus(Payment.Status.FAILED);
            return payment;
        }

        try {
            //External api call to make payment. Change the if statement below to true to fail simulate a failure.
            if (false) {
                throw ApplicationFailure.newNonRetryableFailure("Payment API Failure", TemporalFailure.class.getName());
            }
            payment.setErrorMsg("Payment successful.");
            payment.setStatus(Payment.Status.SUCCESSFUL);
        }
        catch (ApplicationFailure e) {
            payment.setErrorMsg("Customer payment method failed.");
            payment.setStatus(Payment.Status.FAILED);
            return payment;
        }

        payment.setErrorMsg("Payment successful.");
        payment.setStatus(Payment.Status.SUCCESSFUL);

        try {
            db.updatePayment(payment);
        }
        catch (SQLException e) {
            payment.setErrorMsg("Failed to update payment.");
            payment.setStatus(Payment.Status.FAILED);
        }

        return payment;
    }

    @Override
    public void cancelPayment(Payment payment) {

        if (payment.getStatus() == Payment.Status.SUCCESSFUL || payment.getErrorMsg().equals("Failed to update payment.")) {
            //External api call to reverse payment
            System.out.println("Refunded payment.");
        }
        payment.setStatus(Payment.Status.CANCELED);
        try {
            db.updatePayment(payment);
            System.out.println("Canceled payment.");
        }
        catch (SQLException ignored) {}
    }

    @Override
    public Booking confirmBooking(BookingId id) {

        Booking booking = new Booking();
        try {
            db.readBooking(id.getBookingId(), booking);
            booking.setStatus(Booking.Status.CONFIRMED);
            db.updateBooking(booking);
        }
        catch (SQLException e) {
            throw Activity.wrap(e);
        }

        System.out.println("Confirmed booking.");
        return booking;
    }

    @Override
    public void notifyDriver(Booking booking) {

        DriverNotificationRequest driverNotif = new DriverNotificationRequest();
        driverNotif.setBookingId(booking.getBookingId());
        driverNotif.setDriverId(booking.getDriverId());
        driverNotif.setPickUp(booking.getPickUpLocation());
        driverNotif.setDropOff(booking.getDropOffLocation());

        Driver driver =  new Driver();
        try {
            db.readDriver(booking.getDriverId(), driver);
        }
        catch (SQLException e) {
            throw Activity.wrap(e);
        }

        //Notify driver through some api
        System.out.println("Notifying driver " + driver.getName() + " (" + driver.getContact() + ") of confirmed booking...");
    }

    @Override
    public void notifyCustomer(Booking booking) {

        CustomerNotificationRequest customerNotif = new CustomerNotificationRequest();
        customerNotif.setBookingId(booking.getBookingId());
        customerNotif.setRiderId(booking.getDriverId());
        customerNotif.setPickUp(booking.getPickUpLocation());
        customerNotif.setDropOff(booking.getDropOffLocation());

        Rider customer = new Rider();
        try {
            db.readRider(booking.getRiderId(), customer);
        }
        catch (SQLException e) {
            throw Activity.wrap(e);
        }

        //Notify customer through some api
        System.out.println("Notifying customer " + customer.getName() + " (" + customer.getContact() + ") of confirmed booking...");
    }
}
