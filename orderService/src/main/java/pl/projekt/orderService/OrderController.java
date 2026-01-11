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
    public Order createOrder(@RequestBody Order order){
        order.setStatus("PENDING");
        Order savedOrder = orderRepository.save(order);

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE,
                RabbitMqConfig.ROUTING_KEY,
                savedOrder
        );
        return savedOrder;
    }
}
