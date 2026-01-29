📦 System Zamówień (Microservices Order System)

Projekt edukacyjny symulujący backend systemu e-commerce oparty na architekturze mikroserwisów. Aplikacja umożliwia składanie zamówień, asynchroniczne przetwarzanie płatności, generowanie faktur na serwerze FTP oraz wysyłanie powiadomień e-mail.

Całość jest w pełni skonteneryzowana (Docker) i komunikuje się za pomocą RabbitMQ.
🏗️ Architektura i realizacja

System został podzielony na niezależne serwisy (kontenery), które komunikują się ze sobą synchronicznie (REST API) oraz asynchronicznie (Message Broker).


Opis serwisów:

Store Client (CLI): Interfejs konsolowy dla użytkownika. Komunikuje się z backendem przez REST API.
    
Order Service: Serce systemu. Przyjmuje zamówienia, zapisuje je w bazie danych PostgreSQL i po zatwierdzeniu płatności wysyła zdarzenie (Event) na kolejkę RabbitMQ.
    
Payment Service: Konsument RabbitMQ. Odbiera informację o nowym zamówieniu i symuluje procesowanie płatności.
    
Infrastructure Service: Konsument RabbitMQ. Odpowiada za integrację z systemami zewnętrznymi:
    
Generuje fakturę tekstową i wysyła ją na serwer FTP.
    
Wysyła e-mail z potwierdzeniem zamówienia przez serwer SMTP.

🚀 Użyte technologie

Język: Java 21

Framework: Spring Boot 3 (Web, Data JPA, AMQP, Mail)

Konteneryzacja: Docker & Docker Compose

Baza danych: PostgreSQL 15

Message Broker: RabbitMQ 3 (z pluginem Management)

Inne: Alpine FTP Server

📋 Wymagania

Aby uruchomić projekt, potrzebujesz jedynie:

Zainstalowanego środowiska Docker oraz Docker Compose.

Konta pocztowego (np. Gmail) do testowania wysyłki e-mail.

⚙️ Konfiguracja (.env)

W głównym katalogu projektu utwórz plik o nazwie .env. Jest on niezbędny do ustawienia haseł i konfiguracji usług bez ingerencji w kod.

Skopiuj poniższą zawartość, uzupełnij swoje dane, a następnie zapisz plik:


# ==========================================
# 📧 KONFIGURACJA E-MAIL (SMTP)
# ==========================================

# Adres serwera SMTP (np. dla Gmail: smtp.gmail.com, dla Mailtrap: sandbox.smtp.mailtrap.io)
MAIL_HOST=

# Port serwera (np. dla Gmail TLS: 587, dla Mailtrap: 2525)
MAIL_PORT=

# Twój pełny adres e-mail
MAIL_USER=

# Hasło do poczty.
# UWAGA dla Gmaila: Tutaj musisz podać 16-znakowe "Hasło do aplikacji", a nie swoje hasło logowania!
MAIL_PASS=


▶️ Jak uruchomić projekt?
Krok 1: Budowanie i start systemu

Uruchom terminal w folderze projektu i wpisz komendę, która zbuduje obrazy i uruchomi kontenery w tle:
Bash

docker compose up -d --build

    💡 Wskazówka: Poczekaj około 15-20 sekund, aż baza danych i RabbitMQ w pełni wystartują.

Krok 2: Uruchomienie Klienta

Ponieważ klient wymaga interakcji (wpisywania danych z klawiatury), uruchamiamy go osobną komendą w trybie interaktywnym:
Bash

docker compose run --rm store-client

🖥️ Instrukcja obsługi

Po uruchomieniu klienta zobaczysz menu w terminalu. Poruszaj się po nim, wpisując numery opcji.

    Utwórz nowe zamówienie (Koszyk):

        Wpisz ID produktu i ilość.

        Wybierz "Złóż zamówienie", aby wysłać je do Order Service.

        Zamówienie otrzyma status CREATED.

    Zapłać za zamówienie (Realizacja):

        Podaj ID zamówienia.

        Status zmieni się na PAID.

        W tle: Order Service wyśle wiadomość do RabbitMQ. InfraService wygeneruje fakturę na FTP i wyśle do Ciebie prawdziwego maila.

    Pokaż moje zamówienia (Historia):

        Pobiera listę wszystkich zamówień z bazy danych wraz z ich aktualnymi statusami.

🐛 Rozwiązywanie problemów

    Błąd Network ... needs to be recreated Wykonaj komendę resetującą sieć:
    Bash

    docker compose down && docker compose up -d

    FTP nie wstaje (Bad password) Upewnij się, że w pliku .env hasło FTP_PASS ma co najmniej 6 znaków.

    Brak maili Sprawdź logi serwisu infrastruktury wpisując:
    Bash

    docker compose logs infra-service

    Upewnij się, że używasz poprawnego hosta, portu i hasła aplikacji (w przypadku Gmaila).

    Gdzie są faktury? Jeśli skonfigurowałeś wolumen w docker-compose.yml, pliki faktur znajdziesz w folderze ./ftp_data na swoim komputerze.

🛑 Zatrzymywanie aplikacji

Aby bezpiecznie zatrzymać system i usunąć kontenery, wpisz:
Aby bezpiecznie zatrzymać system i usunąć kontenery:
