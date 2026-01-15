package pl.projekt.orderService;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.projekt.orderService.entities.Order;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;

    private final RabbitTemplate rabbitTemplate;
    @Autowired
    public OrderController (OrderRepository orderRepository, RabbitTemplate rabbitTemplate){
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        // 1. Ustawiamy status początkowy
        order.setStatus("PENDING");

        // 2. Zapisujemy w bazie - TO TUTAJ Baza Danych nadaje ID (np. 1)
        Order savedOrder = orderRepository.save(order);

        // 3. Tworzymy obiekt zdarzenia (DTO) zamiast wysyłać całą encję
        // To naprawia błąd w InfraService (ID nie będzie już null)
        OrderEvent event = new OrderEvent(
                savedOrder.getId(),            // <-- Tu bierzemy ID z bazy!
                savedOrder.getCustomerEmail(),
                savedOrder.getAmount(),
                savedOrder.getStatus()
        );
        // 4. Wysyłamy zdarzenie (Event) do RabbitMQ
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE,
                RabbitMqConfig.ROUTING_KEY,
                event // <-- Wysyłamy event, a nie encję
        );

        // 5. Zwracamy obiekt do klienta (API)
        return savedOrder;

    }
}
