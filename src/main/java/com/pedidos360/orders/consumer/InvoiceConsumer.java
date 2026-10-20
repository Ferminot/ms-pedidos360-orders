package com.pedidos360.orders.consumer;

import com.pedidos360.orders.dto.PedidoEventoDTO;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class InvoiceConsumer {

    @RabbitListener(queues = "${pedidos360.rabbitmq.queues.invoice}", ackMode = "MANUAL")
    public void procesarBoleta(PedidoEventoDTO evento, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("[Boleta] Generando boleta para el pedido: {}", evento.getPedidoId());
        try {
            if (evento.isSimularError()) {
                log.error("[Boleta] Error al generar boleta del pedido {}. Derivando a DLQ...", evento.getPedidoId());
                channel.basicNack(tag, false, false);
                return;
            }
            log.info("[Boleta] Boleta emitida con éxito para el cliente: {}", evento.getCliente());
            channel.basicAck(tag, false);
        } catch (Exception e) {
            channel.basicNack(tag, false, true);
        }
    }
}