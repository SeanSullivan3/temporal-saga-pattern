# Temporal Saga Pattern Example
This project is a Temporal implementation of an example [saga pattern by Orkes Conductor](https://github.com/conductor-sdk/conductor-examples-saga-pattern) in Java. In this example saga, a customer will book a cab through a ride service in a four step process. First, the booking is created and put in the booking database. Second, a random cab driver is assigned for the booking and the cab details are put in the cab database. Third, the program will find the customer's payment information from the rider database, put the transaction information into the payment database, and simulate the payment processing. Lastly, the booking will be confirmed and a notifcation will be sent to the driver and customer. If at any point in the workflow the application encounters an error, all completed steps will be compensated for using Temporal's Saga framework.

## Run Instructions

### Environment setup
1. Install JAVA 17 - https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
2. Install Maven 4.0.0 - https://maven.apache.org/download.cgi
3. Install sqlite - https://www.tutorialspoint.com/sqlite/sqlite_installation.htm.
   If using brew, you can just run ```brew install sqlite```

### Running the application

1. Clone this repository

2. Open a terminal and start the temporal server.
```bash
temporal server start-dev
```

3. In second terminal, compile the project and start the Worker.
  -  This terminal will display the log messages as the saga completes.
```bash
mvn clean compile
mvn compile exec:java -Dexec.mainClass="io.temporal.example.saga.workers.SagaWorker"
```

4. In a third terminal, start the spring boot application.
```bash
mvn compile exec:java -Dexec.mainClass="io.temporal.example.saga.workers.SagaWorker"
```

5. In a fourth terminal, run the booking creation command below.
    -  Check your second terminal for workflow updates.
    -  Check `http://localhost:8233/` for the temporal web UI's workflow details.

### Booking creation

To trigger the workflow and begin the saga, use the command below
```
curl --location 'http://localhost:8080/triggerRideBookingFlow' \
--header 'Content-Type: application/json' \
--data '{
  "pickUpLocation": "150 East 52nd Street, New York, NY 10045",
  "dropOffLocation": "120 West 81st Street, New York, NY 10012",
  "riderId": 1
}'
```

### Simulate an error

To simulate the payment failing, and to see the saga compensate, change the return value of the `makePayment()` function in `PaymentService.java` on line 69 from `true` to `false`.

## Diagram

![Flow Chart](SagaFlowChart.png)
