package org.charly.consumerservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQConfig is a configuration class for setting up RabbitMQ messaging in the application.
 *
 * <p>This class defines beans for RabbitMQ components, including:
 * <ul>
 *   <li>Queue</li>
 *   <li>Exchange</li>
 *   <li>Binding</li>
 *   <li>Connection Factory</li>
 *   <li>Message Converter</li>
 *   <li>RabbitTemplate</li>
 * </ul>
 *
 * The configuration values are injected from the application's properties file using
 * Spring's @Value annotation. The RabbitTemplate bean is set up to use JSON message
 * conversion with the Jackson2JsonMessageConverter.
 *
 * <p>This configuration class is annotated with @Configuration, which indicates that it
 * defines Spring beans that are managed by the Spring container.
 *
 * <p>Usage:
 * <pre>{@code
 * @Configuration
 * public class ApplicationConfig {
 *     @Bean
 *     public RabbitMQConfig rabbitMQConfig() {
 *         return new RabbitMQConfig();
 *     }
 * }
 * }</pre>
 */

@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.queue}")
    private String queue;

    @Value("${spring.rabbitmq.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.routingkey}")
    private String routingKey;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.host}")
    private String host;

    // Bean to declare a RabbitMQ Queue
    @Bean
    Queue queue() {
        return new Queue(queue, true);
    }

    // Bean to declare a RabbitMQ Exchange
    @Bean
    Exchange myExchange() {
        return ExchangeBuilder.directExchange(exchange).durable(true).build();
    }

    // Bean to declare a Binding between the queue and the exchange using the routing key
    @Bean
    Binding binding() {
        return BindingBuilder
                .bind(queue())
                .to(myExchange())
                .with(routingKey)
                .noargs();
    }

    // Bean to configure the RabbitMQ Connection Factory
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(host);
        cachingConnectionFactory.setUsername(username);
        cachingConnectionFactory.setPassword(password);
        return cachingConnectionFactory;
    }

    // Bean to define a JSON message converter for RabbitMQ
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Bean to configure the RabbitTemplate for sending messages
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
