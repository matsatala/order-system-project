package pl.projekt.infrastructureService;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            try {
                emailService.sendEmail(
                        event.customerEmail(),
                        event.orderId().toString(),
                        event.amountEur()
                );
            }catch (Exception e){
                System.err.println("Błąd wysyłki maila: "+e.getMessage());
            }
            try {
                String invoiceContent = "FAKTURA VAT\n" +
                        "Zamówienie: " + event.orderId() + "\n" +
                        "Klient: " + event.customerEmail() + "\n" +
                        "Kwota: " + event.amountEur() + " EUR\n" +
                        "Status: OPŁACONE";

                ftpService.uploadInvoice(event.orderId().toString(),invoiceContent);
            }catch (Exception e){
                System.err.println(e.getMessage());
            }
        } else {
            System.out.println("Płatność nieudana");
        }


    }
}
