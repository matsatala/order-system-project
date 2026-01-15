package pl.projekt.orderService;

public record OrderEvent(
        Long orderId,
        String customerEmail,
        Double amount,
        String status
) {}