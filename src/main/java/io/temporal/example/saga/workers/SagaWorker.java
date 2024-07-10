package io.temporal.example.saga.workers;

import io.temporal.client.WorkflowClient;
import io.temporal.example.saga.activities.SagaActivitiesImpl;
import io.temporal.example.saga.workflows.SagaWorkflowImpl;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class SagaWorker {

    public static void main(String[] args) {

        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);
        WorkerFactory factory = WorkerFactory.newInstance(client);

        Worker worker = factory.newWorker("ride-service");

        worker.registerWorkflowImplementationTypes(SagaWorkflowImpl.class);
        worker.registerActivitiesImplementations(new SagaActivitiesImpl());

        factory.start();
    }
}
