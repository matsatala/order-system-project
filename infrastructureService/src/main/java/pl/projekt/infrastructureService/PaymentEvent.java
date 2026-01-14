package pl.projekt.infrastructureService;

import java.io.Serializable;

public record PaymentEvent(
        Long orderId,
        String customerEmail,
        Double amountEur,
        PaymentStatus status

) implements Serializable {
}
