package pl.projekt.orderService;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentStatusListener {

    private final OrderRepository orderRepository;

    public PaymentStatusListener(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Nasłuchujemy na naszej nowej kolejce
    @RabbitListener(queues = RabbitMqConfig.ORDER_STATUS_QUEUE)
    public void handlePaymentStatus(PaymentEvent event) {
        System.out.println(" [Order Service] Otrzymano info o płatności dla ID: " + event.orderId());

        // Sprawdzamy, czy płatność się udała
        // (W zależności od tego czy używasz Enuma czy Stringa w PaymentService, dostosuj ten warunek)
        if (PaymentStatus.SUCCESS.equals(event.status())) {

            orderRepository.findById(event.orderId()).ifPresent(order -> {
                order.setStatus("PAID"); // <--- Zmiana statusu na ostateczny
                orderRepository.save(order);
                System.out.println(" [Order Service] Status zamówienia " + event.orderId() + " zmieniony na PAID.");
            });

        } else {
            System.out.println(" [Order Service] Płatność nieudana, status bez zmian.");
        }
    }
}