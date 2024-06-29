package ride.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import ride.models.*;

@ActivityInterface
public interface RideActivities {

    @ActivityMethod
    public BookingId createBooking(BookingRequest request);

    @ActivityMethod
    public void cancelBooking(BookingId id);

    @ActivityMethod
    public PaymentRequest assignDriver(BookingId id);

    @ActivityMethod
    public void cancelAssignment(BookingId id);

    @ActivityMethod
    public Payment makePayment(PaymentRequest request);

    @ActivityMethod
    public void cancelPayment(Payment payment);

    @ActivityMethod
    public Booking confirmBooking(BookingId bookingId);

    @ActivityMethod
    public void notifyDriver(Booking booking);

    @ActivityMethod
    public void notifyCustomer(Booking booking);
}
