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
public class CocinaConsumer {

    @RabbitListener(queues = "${pedidos360.rabbitmq.queues.kitchen}", ackMode = "MANUAL")
    public void procesarOrdenCocina(PedidoEventoDTO evento, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("[Cocina] Procesando comanda para el pedido: {}", evento.getPedidoId());

        try {
            if (evento.isSimularError()) {
                log.error("[Cocina] Error no recuperable detectado en el pedido {}. Enviando a DLQ...", evento.getPedidoId());
                // NACK explícito (requeue = false) para redirigir el mensaje hacia la DLQ asignada
                channel.basicNack(tag, false, false);
                return;
            }

            log.info("[Cocina] Ticket impreso con éxito para cliente: {}", evento.getCliente());
            // ACK explícito indicando procesamiento exitoso
            channel.basicAck(tag, false);

        } catch (Exception e) {
            log.warn("[Cocina] Error recuperable al procesar comanda. Reintentando...", e);
            // NACK explícito (requeue = true) para intentar procesar nuevamente
            channel.basicNack(tag, false, true);
        }
    }
}