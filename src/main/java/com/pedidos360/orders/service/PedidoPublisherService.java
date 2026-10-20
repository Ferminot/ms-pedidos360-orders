package com.pedidos360.orders.service;

import com.pedidos360.orders.config.RabbitMQProperties;
import com.pedidos360.orders.dto.PedidoEventoDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PedidoPublisherService {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties props;

    public PedidoPublisherService(RabbitTemplate rabbitTemplate, RabbitMQProperties props) {
        this.rabbitTemplate = rabbitTemplate;
        this.props = props;
    }

    public void publicarEvento(PedidoEventoDTO evento) {
        String routingKey;

        // Determinar la routing key en base al tipo de evento
        switch (evento.getTipoEvento()) {
            case "PedidoAceptado" -> routingKey = props.getRoutingKeys().getKitchen();
            case "PedidoDespachado" -> routingKey = props.getRoutingKeys().getInvoice();
            default -> routingKey = props.getRoutingKeys().getEmail();
        }

        // Publicar al Direct Exchange centralizado
        rabbitTemplate.convertAndSend(props.getExchanges().getDirect(), routingKey, evento);
    }
}