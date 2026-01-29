package pl.projekt.paymentService;



import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;


@Configuration
public class RabbitMqConfig {
    @Bean
    public Queue orderQueue(){
        return new Queue("order_queue");
    }


    @Bean
    public MessageConverter jsonMessageConverter() {

            var jsonMapper = JsonMapper.builder()
                    .findAndAddModules()
                    .build();

            JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter(jsonMapper);

            converter.setTypePrecedence(JacksonJavaTypeMapper.TypePrecedence.INFERRED);

            return converter;
    }
}
