package pl.projekt.infrastructureService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender){
        this.mailSender =mailSender;
    }

    public void sendEmail(String to, String orderId, Double amount){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sklep@mikroserwisy.pl");
        message.setTo(to);
        message.setSubject("Potwierdzenie zamówienia: "+orderId);
        message.setText("Twoje zamówienie zostało opłacone. Kwota: "+String.format("%.2f", amount)+ " EUR");

        mailSender.send(message);
        System.out.println("email wysłany do: "+to);
    }

}
