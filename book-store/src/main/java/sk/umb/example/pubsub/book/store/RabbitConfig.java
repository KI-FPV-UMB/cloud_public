package sk.umb.example.pubsub.book.store;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.MessageConverter;

@EnableRabbit
@Configuration
public class RabbitConfig {
    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();

        // Define the allowed classes for deserialization
        javaTypeMapper.setTrustedPackages("java.util", "sk.umb.example.pubsub.book.store"); // add your package name

        converter.setJavaTypeMapper(javaTypeMapper);
        return converter;
    }


    @Bean
    public TopicExchange bookExchange() {
        return new TopicExchange("bookExchange");
    }

    @Bean
    public Queue bookQueue() {
        return new Queue("bookQueue");
    }

    @Bean
    public Binding binding(Queue bookQueue, TopicExchange bookExchange) {
        return BindingBuilder.bind(bookQueue).to(bookExchange).with("book.#");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
