package com.pedidos360.orders.service;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;

@Service
public class RabbitAdminService {

    private final RabbitAdmin rabbitAdmin;

    public RabbitAdminService(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    public void crearColaDurable(String nombreCola) {
        Queue queue = QueueBuilder.durable(nombreCola).build();
        rabbitAdmin.declareQueue(queue);
    }

    public boolean eliminarCola(String nombreCola) {
        return rabbitAdmin.deleteQueue(nombreCola);
    }

    public void crearExchangeDirect(String nombreExchange) {
        DirectExchange exchange = new DirectExchange(nombreExchange);
        rabbitAdmin.declareExchange(exchange);
    }

    public boolean eliminarExchange(String nombreExchange) {
        return rabbitAdmin.deleteExchange(nombreExchange);
    }

    public void crearBinding(String nombreCola, String nombreExchange, String routingKey) {
        Queue queue = new Queue(nombreCola);
        DirectExchange exchange = new DirectExchange(nombreExchange);
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(routingKey);
        rabbitAdmin.declareBinding(binding);
    }
}