package pl.projekt.orderService;

import java.io.Serializable;

public record PaymentEvent(
        Long orderId,
        String customerEmail,
        Double amountEur,
        PaymentStatus status
) implements Serializable {
}
