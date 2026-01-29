package pl.projekt.orderService;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.projekt.orderService.entities.Order;

import java.util.List;

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
        order.setStatus("UNPAID");

        return orderRepository.save(order);
    }

    @PostMapping("/{id}/pay")
    public String payOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zamówienie nie istnieje!"));

        if (!"UNPAID".equals(order.getStatus())) {
            return "Zamówienie zostało już opłacone lub jest w trakcie przetwarzania.";
        }

        // Zmieniamy status
        order.setStatus("PENDING");
        Order savedOrder = orderRepository.save(order);

        OrderEvent event = new OrderEvent(
                savedOrder.getId(),
                savedOrder.getCustomerEmail(),
                savedOrder.getAmount(),
                savedOrder.getStatus()
        );

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE,
                RabbitMqConfig.ROUTING_KEY,
                event
        );

        return "Zamówienie nr " + id + " zostało opłacone i wysłane do realizacji.";
    }
    @GetMapping("/{email}")
    public List<Order> getOrdersByEmail(@PathVariable String email) {
        return orderRepository.findByCustomerEmail(email);
    }
}
