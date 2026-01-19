package pl.projekt.orderService;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RabbitMqConfig {
    // dane do obsługi orderów
    public static final String EXCHANGE = "order_exchange";
    public static final String QUEUE = "order_queue";
    public static final String ROUTING_KEY = "order_routing_key";
    // dane do obsługi
    public static final String PAYMENT_EXCHANGE = "payment_exchange";
    public static final String PAYMENT_ROUTING_KEY = "payment_routing_key";
    public static final String ORDER_STATUS_QUEUE = "order_payment_status_update_queue"; // Unikalna nazwa

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue queue() {
        return new Queue(QUEUE);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public Queue paymentStatusQueue() {
        // Kolejka dedykowana dla Order Service
        return new Queue(ORDER_STATUS_QUEUE);
    }

    @Bean
    public TopicExchange paymentExchange() {
        // Deklarujemy exchange płatności, żeby móc się do niego podpiąć
        return new TopicExchange(PAYMENT_EXCHANGE);
    }

    @Bean
    public Binding paymentBinding(Queue paymentStatusQueue, TopicExchange paymentExchange) {
        // Łączymy naszą kolejkę z exchange'em płatności
        return BindingBuilder.bind(paymentStatusQueue).to(paymentExchange).with(PAYMENT_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        var jsonMapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        return new JacksonJsonMessageConverter(jsonMapper);
    }

}
