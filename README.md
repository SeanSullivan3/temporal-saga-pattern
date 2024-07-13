<p><a target="_blank" href="https://app.eraser.io/workspace/zH8u7jsYJsNPrsoeE8PQ" id="edit-in-eraser-github-link"><img alt="Edit in Eraser" src="https://firebasestorage.googleapis.com/v0/b/second-petal-295822.appspot.com/o/images%2Fgithub%2FOpen%20in%20Eraser.svg?alt=media&amp;token=968381c8-a7e7-472a-8ed6-4a6626da5501"></a></p>

# Temporal Saga Pattern Example
This project is a Temporal implementation of an example [﻿saga pattern by Orkes Conductor](https://github.com/conductor-sdk/conductor-examples-saga-pattern) in Java. In this example saga, a customer will book a cab through a ride service in a four step process. First, the booking is created and put in the booking database. Second, a random cab driver is assigned for the booking and the cab details are put in the cab database. Third, the program will find the customer's payment information from the rider database, put the transaction information into the payment database, and simulate the payment processing. Lastly, the booking will be confirmed and a notifcation will be sent to the driver and customer. If at any point in the workflow the application encounters an error, all completed steps will be compensated for using Temporal's Saga framework.

## Run Instructions
### Environment setup
1. Install JAVA 17 - [﻿https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) 
2. Install Maven 4.0.0 - [﻿https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi) 
3. Install sqlite - [﻿https://www.tutorialspoint.com/sqlite/sqlite_installation.htm](https://www.tutorialspoint.com/sqlite/sqlite_installation.htm) .
If using brew, you can just run `brew install sqlite` 
### Running the application
1. Clone this repository
2. Open a terminal and start the temporal server.
```bash
temporal server start-dev
```
1. In second terminal, compile the project and start the Worker.
- This terminal will display the log messages as the saga completes.
```bash
mvn clean compile
mvn compile exec:java -Dexec.mainClass="io.temporal.example.saga.workers.SagaWorker"
```
1. In a third terminal, start the spring boot application.
```bash
mvn compile exec:java -Dexec.mainClass="io.temporal.example.saga.workers.SagaWorker"
```
1. In a fourth terminal, run the booking creation command below.
    - Check your second terminal for workflow updates.
    - Check `http://localhost:8233/`  for the temporal web UI's workflow details.
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




<!-- eraser-additional-content -->
## Diagrams
<!-- eraser-additional-files -->
<a href="/README-flowchart-1.eraserdiagram" data-element-id="Rut-D4zjPKkGS90fxpNnZ"><img src="/.eraser/zH8u7jsYJsNPrsoeE8PQ___3S81KJ4sa8RWOkPCr1eDghU5uCT2___---diagram----b8f45da0f41eff7db052e18f93eb9f55.png" alt="" data-element-id="Rut-D4zjPKkGS90fxpNnZ" /></a>
<!-- end-eraser-additional-files -->
<!-- end-eraser-additional-content -->
<!--- Eraser file: https://app.eraser.io/workspace/zH8u7jsYJsNPrsoeE8PQ --->