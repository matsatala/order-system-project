package pl.projekt.paymentService;


import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMqConfig {
    @Bean
    public Queue orderQueue(){
        return new Queue("order_queue");
    }

}
