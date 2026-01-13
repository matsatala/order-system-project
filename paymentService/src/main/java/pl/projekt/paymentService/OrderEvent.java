package pl.projekt.paymentService;

import lombok.Getter;


public record OrderEvent(
        Long orderId,
        String customerEmail,
        Double amount,
        String status
) {}