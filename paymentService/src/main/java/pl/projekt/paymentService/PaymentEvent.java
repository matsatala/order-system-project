package pl.projekt.paymentService;

import java.io.Serializable;

public record PaymentEvent(
        Long orderId,
        String customerEmail,
        Double amountEur,
        String status
) implements Serializable {
}
