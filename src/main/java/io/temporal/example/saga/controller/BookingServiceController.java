package io.temporal.example.saga.controller;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.example.saga.pojos.BookingRequest;
import io.temporal.example.saga.workflows.SagaWorkflow;
import io.temporal.workflow.Workflow;
import io.temporal.serviceclient.WorkflowServiceStubs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class BookingServiceController {

    @PostMapping(value = "/triggerRideBookingFlow", produces = "application/json")
    public ResponseEntity<String> triggerRideBookingFlow(@RequestBody BookingRequest bookingRequest) {

        UUID uuid = Workflow.randomUUID();
        String uuidAsString = uuid.toString();
        bookingRequest.setBookingId(uuidAsString);

        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setWorkflowId(uuidAsString)
                .setTaskQueue("ride-service")
                .build();

        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);
        SagaWorkflow workflow = client.newWorkflowStub(SagaWorkflow.class, options);
        return ResponseEntity.ok(workflow.rideService(bookingRequest));
    }
}
