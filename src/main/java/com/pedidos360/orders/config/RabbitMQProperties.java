package com.pedidos360.orders.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "pedidos360.rabbitmq")
public class RabbitMQProperties {

    private Exchanges exchanges = new Exchanges();
    private Queues queues = new Queues();
    private RoutingKeys routingKeys = new RoutingKeys();

    @Data
    public static class Exchanges {
        private String direct;
        private String topic;
        private String dlx;
    }

    @Data
    public static class Queues {
        private String email;
        private String emailDlq;
        private String kitchen;
        private String kitchenDlq;
        private String invoice;
        private String invoiceDlq;
    }

    @Data
    public static class RoutingKeys {
        private String email;
        private String kitchen;
        private String invoice;
    }
}