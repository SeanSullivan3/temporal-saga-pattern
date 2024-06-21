package ride;

import io.temporal.client.WorkflowOptions;
import ride.models.BookingRequest;
import io.temporal.client.WorkflowClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ride.workflows.RideWorkflow;

import java.util.Map;
import java.util.UUID;

@RestController
public class Controller {

    @Autowired
    WorkflowClient client;

    @PostMapping(value = "/triggerRideBookingFlow", produces = "application/json")
    public ResponseEntity<String> triggerRideBookingFlow(@RequestBody BookingRequest bookingRequest) {

        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        bookingRequest.setBookingId(uuidAsString);

        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setWorkflowId(uuidAsString)
                .setTaskQueue("ride-service")
                .build();

        RideWorkflow workflow = client.newWorkflowStub(RideWorkflow.class, options);
        return ResponseEntity.ok(workflow.rideService(bookingRequest));
    }
}
