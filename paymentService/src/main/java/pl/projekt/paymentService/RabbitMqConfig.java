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

            // 1. Używamy nowoczesnego buildera dla Jacksona (tzw. Jackson 3 way)
            var jsonMapper = JsonMapper.builder()
                    .findAndAddModules() // Obsługa dat (Java 8 Time)
                    .build();

            // 2. Tworzymy konwerter z naszym mapperem
            JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter(jsonMapper);

            // 3. KLUCZOWE: Mówimy konwerterowi:
            // "Nie patrz na nagłówki wiadomości (__TypeId__).
            // Użyj typu, który jest zadeklarowany w metodzie @RabbitListener".
            converter.setTypePrecedence(JacksonJavaTypeMapper.TypePrecedence.INFERRED);

            return converter;
    }
}
