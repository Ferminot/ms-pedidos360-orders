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
public class EmailConsumer {

    @RabbitListener(queues = "${pedidos360.rabbitmq.queues.email}", ackMode = "MANUAL")
    public void procesarNotificacionEmail(PedidoEventoDTO evento, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("[Email] Enviando correo de confirmación para el pedido: {}", evento.getPedidoId());
        try {
            if (evento.isSimularError()) {
                log.error("[Email] Error al enviar correo del pedido {}. Derivando a DLQ...", evento.getPedidoId());
                channel.basicNack(tag, false, false);
                return;
            }
            log.info("[Email] Correo enviado exitosamente a: {}", evento.getCliente());
            channel.basicAck(tag, false);
        } catch (Exception e) {
            channel.basicNack(tag, false, true);
        }
    }
}