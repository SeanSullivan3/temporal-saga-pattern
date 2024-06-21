package ride.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import ride.models.BookingId;
import ride.models.BookingRequest;
import ride.models.Payment;
import ride.models.PaymentRequest;

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
    public void cancelPayment(PaymentRequest request);

    @ActivityMethod
    public void notify(BookingId id);
}
