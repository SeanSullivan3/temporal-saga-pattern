package ride.workflows;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import ride.models.Booking;
import ride.models.BookingRequest;

@WorkflowInterface
public interface RideWorkflow {

    @WorkflowMethod
    public String rideService(BookingRequest request);
}
