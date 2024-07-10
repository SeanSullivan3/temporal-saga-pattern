package io.temporal.example.saga.workflows;

import io.temporal.example.saga.pojos.BookingRequest;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface SagaWorkflow {

    @WorkflowMethod
    public String rideService(BookingRequest request);
}
