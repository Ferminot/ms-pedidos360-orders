package com.pedidos360.orders.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private final RabbitMQProperties props;

    public RabbitMQConfig(RabbitMQProperties props) {
        this.props = props;
    }

    // --- CONVERSOR A JSON (Resuelve la deserialización) ---
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    // --- EXCHANGES ---
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(props.getExchanges().getDirect());
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(props.getExchanges().getTopic());
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(props.getExchanges().getDlx());
    }

    // --- 1. COLAS DLQ (DEAD LETTER QUEUES) ---
    @Bean
    public Queue emailDlq() {
        return QueueBuilder.durable(props.getQueues().getEmailDlq()).build();
    }

    @Bean
    public Queue kitchenDlq() {
        return QueueBuilder.durable(props.getQueues().getKitchenDlq()).build();
    }

    @Bean
    public Queue invoiceDlq() {
        return QueueBuilder.durable(props.getQueues().getInvoiceDlq()).build();
    }

    // --- 2. COLAS PRINCIPALES CON DLX Y ROUTING KEY DE DLQ ---
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(props.getQueues().getEmail())
                .deadLetterExchange(props.getExchanges().getDlx())
                .deadLetterRoutingKey(props.getQueues().getEmailDlq())
                .build();
    }

    @Bean
    public Queue kitchenQueue() {
        return QueueBuilder.durable(props.getQueues().getKitchen())
                .deadLetterExchange(props.getExchanges().getDlx())
                .deadLetterRoutingKey(props.getQueues().getKitchenDlq())
                .build();
    }

    @Bean
    public Queue invoiceQueue() {
        return QueueBuilder.durable(props.getQueues().getInvoice())
                .deadLetterExchange(props.getExchanges().getDlx())
                .deadLetterRoutingKey(props.getQueues().getInvoiceDlq())
                .build();
    }

    // --- 3. BINDINGS PARA COLAS PRINCIPALES (Exchanges de entrada) ---
    @Bean
    public Binding bindingEmail(Queue emailQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(emailQueue).to(directExchange).with(props.getRoutingKeys().getEmail());
    }

    @Bean
    public Binding bindingKitchen(Queue kitchenQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(kitchenQueue).to(directExchange).with(props.getRoutingKeys().getKitchen());
    }

    @Bean
    public Binding bindingInvoice(Queue invoiceQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(invoiceQueue).to(directExchange).with(props.getRoutingKeys().getInvoice());
    }

    // --- 4. BINDINGS PARA COLAS DLQ (Dead Letter Exchange) ---
    @Bean
    public Binding bindingKitchenDlq(Queue kitchenDlq, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(kitchenDlq).to(deadLetterExchange).with(props.getQueues().getKitchenDlq());
    }

    @Bean
    public Binding bindingEmailDlq(Queue emailDlq, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(emailDlq).to(deadLetterExchange).with(props.getQueues().getEmailDlq());
    }

    @Bean
    public Binding bindingInvoiceDlq(Queue invoiceDlq, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(invoiceDlq).to(deadLetterExchange).with(props.getQueues().getInvoiceDlq());
    }

    // --- 5. INICIALIZADOR EXPLÍCITO DE INFRAESTRUCTURA ---
    @Bean
    public ApplicationRunner runner(RabbitAdmin rabbitAdmin) {
        return args -> rabbitAdmin.initialize();
    }
}