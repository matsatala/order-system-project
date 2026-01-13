package pl.projekt.paymentService;


import lombok.Getter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

@Service
public class CurrencyService {

    private final RestClient restClient = RestClient.create();
    @Getter
    private Double lastKnownRate = 4.2127;

    @Scheduled(fixedRate = 60000) // Wykonuj co 60 000 ms (1 minuta)
    private void refreshRate() {
        System.out.println("Automatyczne odświeżanie kursu walut...");
        String url = "https://api.nbp.pl/api/exchangerates/rates/a/eur?format=json";
        try {
            NbpResponse response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(NbpResponse.class);

            if (response != null && response.rates() != null && !response.rates().isEmpty()) {

                lastKnownRate = response.rates().getFirst().mid();
            }
        } catch (Exception e) {
            System.err.println("Problem z API NBP: " + e.getMessage());
        }
    }

}
