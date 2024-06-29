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
            System.out.println("Booking requested by rider " + request.getRiderId() + ".\nPick up: " + request.getPickUpLocation() + ".\nDrop off: " + request.getDropOffLocation());
            BookingId bookingId = activities.createBooking(request);
            saga.addCompensation(activities::cancelBooking, bookingId);

            //Assign a driver
            saga.addCompensation(activities::cancelAssignment, bookingId);
            PaymentRequest paymentRequest = activities.assignDriver(bookingId);

            //Make payment
            UUID uuid = UUID.randomUUID();
            String uuidAsString = uuid.toString();
            paymentRequest.setPaymentId(uuidAsString);
            Payment payment = activities.makePayment(paymentRequest);
            saga.addCompensation(activities::cancelPayment, payment);
            System.out.println(payment.getErrorMsg());


            if (payment.getStatus() == Payment.Status.SUCCESSFUL) {
                //Confirm and notify
                Booking confirmedBooking = activities.confirmBooking(bookingId);
                activities.notifyDriver(confirmedBooking);
                activities.notifyCustomer(confirmedBooking);
            }
            else {
                saga.compensate();
                return "Booking failed. Saga compensated.";
            }
        }
        catch (TemporalFailure e) {
            saga.compensate();
            return "Booking failed. Saga compensated.";
        }
        return "Successful ride.";
    }
}
