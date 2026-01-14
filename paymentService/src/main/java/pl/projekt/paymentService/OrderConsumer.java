package pl.projekt.paymentService;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {

    private final RabbitTemplate rabbitTemplate;
    private final CurrencyService currencyService;

    @Autowired
    public OrderConsumer(RabbitTemplate rabbitTemplate,CurrencyService currencyService){
        this.rabbitTemplate = rabbitTemplate;
        this.currencyService = currencyService;
    }
    @RabbitListener(queues =  "order_queue")
    public void handleOrder(OrderEvent event){
        Double rate = currencyService.getLastKnownRate();
        Double totalEur = event.amount()/rate;

        var confirmation = new PaymentEvent(event.orderId(), event.customerEmail(),totalEur, PaymentStatus.SUCCESS);

        rabbitTemplate.convertAndSend("payment_exchange","payment_routing_key",confirmation);

        System.out.println("Płatność przetworzona. Wysłano potwierdzenie dla: " + event.orderId());
    }
}
