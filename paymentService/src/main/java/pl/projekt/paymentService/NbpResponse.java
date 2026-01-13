package pl.projekt.paymentService;

import java.util.List;

public record NbpResponse(
        String table,
        String currency,
        String code,
        List<Rate> rates
) {}
