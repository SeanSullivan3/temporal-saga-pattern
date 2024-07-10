package io.temporal.example.saga.service;

import io.temporal.example.saga.dao.PaymentsDAO;
import io.temporal.example.saga.pojos.Payment;
import io.temporal.example.saga.pojos.PaymentMethod;
import io.temporal.example.saga.pojos.PaymentRequest;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class PaymentService {

    private static final PaymentsDAO paymentsDAO = new PaymentsDAO("jdbc:sqlite:cab_saga.db");

    public static Payment createPayment(PaymentRequest paymentRequest) {
        UUID uuid = Workflow.randomUUID();
        String uuidAsString = uuid.toString();

        int riderId = paymentRequest.getRiderId();

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentsDAO.readPaymentMethod(riderId, paymentMethod);

        Payment payment = new Payment();
        payment.setPaymentId(uuidAsString);
        payment.setBookingId(paymentRequest.getBookingId());
        payment.setAmount(40.0);
        payment.setPaymentMethodId(0);
        payment.setStatus(Payment.Status.PENDING);

        if (!paymentsDAO.insertPayment(payment).isEmpty()) {
            log.error("Failed to process payment for booking {}", paymentRequest.getBookingId());
            payment.setErrorMsg("Payment creation failure");
            payment.setStatus(Payment.Status.FAILED);
        }
        else if (paymentMethod.getId() > 0) {
            payment.setPaymentMethodId(paymentMethod.getId());
            if(makePayment(paymentMethod)) {
                payment.setStatus(Payment.Status.SUCCESSFUL);
            }
            else {
                log.error("Payment method for rider {} unsuccessful, failed to pay for booking {}", riderId, paymentRequest.getBookingId());
                payment.setErrorMsg("Rider's payment method failed");
                payment.setStatus(Payment.Status.FAILED);
            }
        } else {
            log.error("Payment method for rider {} not available, failed to pay for booking {}", riderId, paymentRequest.getBookingId());
            payment.setErrorMsg("Rider doesn't have a payment method added");
            payment.setStatus(Payment.Status.FAILED);
        }

        paymentsDAO.updatePayment(payment);

        return payment;
    }

    public static void cancelPayment(Payment payment) {
        payment.setStatus(Payment.Status.CANCELED);
        paymentsDAO.updatePayment(payment);
        log.error("Canceled payment with id: {}", payment.getPaymentId());
    }

    private static boolean makePayment(PaymentMethod paymentMethod) {
        // Call external Payments API
        log.error("Making payment for rider {} with payment method {}", paymentMethod.getRiderId(), paymentMethod.getDetails());
        // To simulate a failed payment, change the return value to false
        return true;
    }
}
