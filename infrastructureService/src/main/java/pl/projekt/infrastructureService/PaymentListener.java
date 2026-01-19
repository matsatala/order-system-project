package pl.projekt.infrastructureService;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PaymentListener {

    private final EmailService emailService;
    private final FtpService ftpService;

    @Autowired
    public PaymentListener(EmailService emailService, FtpService ftpService){
        this.emailService = emailService;
        this.ftpService = ftpService;
    }

    @RabbitListener(queues = RabbitMQInfraConfig.QUEUE)
    public void handlePayment(PaymentEvent event){

        System.out.println("---Otrzymano potwierdzenie płatności dla zamówienia: "+event.orderId()+"---");

        if (PaymentStatus.SUCCESS.equals(event.status())){
            String invoiceContent = "FAKTURA VAT\n" +
                    "Zamówienie: " + event.orderId() + "\n" +
                    "Klient: " + event.customerEmail() + "\n" +
                    "Kwota: " + new BigDecimal(event.amountEur()).setScale(2, RoundingMode.HALF_EVEN) + " EUR\n" +
                    "Status: OPŁACONE";
            String filename = "faktura_"+event.orderId()+".txt";
            try {
                ftpService.uploadInvoice(event.orderId().toString(),invoiceContent,filename);
            }catch (Exception e){
                System.err.println(e.getMessage());
            }
            try {
                emailService.sendEmail(
                        event.customerEmail(),
                        event.orderId().toString(),
                        event.amountEur(),
                        invoiceContent,
                        filename
                );
            }catch (Exception e){
                System.err.println("Błąd wysyłki maila: "+e.getMessage());
            }

        } else {
            System.out.println("Płatność nieudana");
        }


    }
}
