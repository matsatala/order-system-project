package pl.projekt.infrastructureService;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender){
        this.mailSender =mailSender;
    }

    public void sendEmail(String to, String orderId, Double amount,String attachmentContent,String filename){
        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            message.setFrom("sklep@mikroserwisy.pl");
            helper.setTo(to);
            helper.setSubject("Potwierdzenie zamówienia: " + orderId);
            helper.setText("Twoje zamówienie zostało opłacone. Kwota: " + String.format("%.2f", amount) + " EUR\n" +
                    "Ta wiadomość została wygenerowana automatycznie,\n" +
                    "proszę na nią nie odpowiadać.");

            ByteArrayResource resource = new ByteArrayResource(attachmentContent.getBytes());
            helper.addAttachment(filename,resource);

            mailSender.send(message);
            System.out.println("email wysłany do: " + to);
        } catch (MessagingException e) {
            System.err.println("Błąd podczas tworzenia maila "+ e.getMessage());
        }
    }

}
