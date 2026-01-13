package pl.projekt.paymentService;

public record Rate(
        String no,
        String effectiveDate,
        Double mid
) {}
