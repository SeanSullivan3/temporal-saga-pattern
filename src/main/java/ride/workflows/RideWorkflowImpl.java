package ride.workflows;

import io.temporal.activity.ActivityOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.failure.ApplicationFailure;
import io.temporal.failure.TemporalFailure;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;
import ride.activities.RideActivities;
import ride.models.*;

import java.time.Duration;
import java.util.UUID;

@WorkflowImpl(workers = "saga-pattern-worker")
public class RideWorkflowImpl implements RideWorkflow {

    private final ActivityOptions options =
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(5))
                    .build();

    private final RideActivities activities =
            Workflow.newActivityStub(RideActivities.class, options);

    @Override
    public String rideService(BookingRequest request) {

        Saga saga = new Saga(new Saga.Options.Builder().build());
        try {
            //Book a ride
            BookingId bookingId = activities.createBooking(request);
            saga.addCompensation(activities::cancelBooking, bookingId);

            //Assign a random driver
            saga.addCompensation(activities::cancelAssignment, bookingId);
            PaymentRequest paymentRequest = activities.assignDriver(bookingId);

            //Make payment
            UUID uuid = UUID.randomUUID();
            String uuidAsString = uuid.toString();
            paymentRequest.setPaymentId(uuidAsString);
            saga.addCompensation(activities::cancelPayment, paymentRequest);
            Payment payment = activities.makePayment(paymentRequest);

            //Confirm and notify
            if (payment.getStatus() == Payment.Status.SUCCESSFUL) {
                activities.notify(bookingId);
            }
            else {
                saga.compensate();
                return payment.getErrorMsg();
            }
        }
        catch (TemporalFailure e) {
            saga.compensate();
            return "Booking failed.";
        }
        return "Successful ride.";
    }
}
