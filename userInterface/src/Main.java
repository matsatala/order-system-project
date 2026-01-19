import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {

    private static final String API_URL = "http://localhost:8081/api/orders";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HttpClient client = HttpClient.newHttpClient();

        System.out.println("=========================================");
        System.out.println("   SYSTEM ZAMÓWIEŃ v2.0 (CLI)   ");
        System.out.println("=========================================");

        while (true) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Utwórz nowe zamówienie (Koszyk)");
            System.out.println("2. Zapłać za zamówienie (Realizacja)");
            System.out.println("3. Pokaż moje zamówienia (Historia)");
            System.out.println("4. Wyjście");
            System.out.print("Wybierz opcję > ");

            String choice = scanner.nextLine();

            if ("4".equals(choice)) break;

            switch (choice) {
                case "1" -> createOrder(scanner, client);
                case "2" -> payOrder(scanner, client);
                case "3" -> listOrders(scanner, client);
                default -> System.out.println("Nieznana opcja.");
            }
        }
    }

    // Opcja 1: Tylko zapis do bazy (status UNPAID)
    private static void createOrder(Scanner scanner, HttpClient client) {
        try {
            System.out.print("Podaj e-mail: ");
            String email = scanner.nextLine();
            System.out.print("Podaj kwotę: ");
            String amount = scanner.nextLine().replace(",", ".");

            String jsonBody = String.format("{\"customerEmail\": \"%s\", \"amount\": %s}", email, amount);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("✅ Zamówienie utworzone! Odpowiedź serwera:");
            System.out.println(response.body());
            System.out.println("PAMIĘTAJ ID ZAMÓWIENIA, ABY JE OPŁACIĆ!");

        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    // Opcja 2: Wysłanie sygnału płatności -> RabbitMQ
    private static void payOrder(Scanner scanner, HttpClient client) {
        try {
            System.out.print("Podaj ID zamówienia do opłacenia: ");
            String id = scanner.nextLine();

            // Używamy POST na endpoint /api/orders/{id}/pay
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/" + id + "/pay"))
                    .POST(HttpRequest.BodyPublishers.noBody()) // Pusty POST, bo ID jest w URL
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("💸 Wynik płatności:");
            System.out.println(response.body());

        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    // Opcja 3: Pobranie listy z bazy
    private static void listOrders(Scanner scanner, HttpClient client) {
        try {
            System.out.print("Podaj e-mail do sprawdzenia: ");
            String email = scanner.nextLine();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/" + email))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("📜 Historia zamówień dla: " + email);
            // Wyświetlamy surowy JSON (w prawdziwej aplikacji użylibyśmy biblioteki Jackson do formatowania)
            System.out.println(response.body());

        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }
}