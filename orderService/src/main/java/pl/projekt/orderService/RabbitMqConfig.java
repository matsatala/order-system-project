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

    public static final String EXCHANGE = "order_exchange";
    public static final String QUEUE = "order_queue";
    public static final String ROUTING_KEY = "order_routing_key";

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
    public MessageConverter jsonMessageConverter() {
        var jsonMapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        return new JacksonJsonMessageConverter(jsonMapper);
    }

}
