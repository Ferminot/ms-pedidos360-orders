package com.pedidos360.orders.controller;

import com.pedidos360.orders.dto.PedidoEventoDTO;
import com.pedidos360.orders.service.PedidoPublisherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoPublisherService publisherService;

    public PedidoController(PedidoPublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @PostMapping("/evento")
    public ResponseEntity<String> publicarEvento(@RequestBody PedidoEventoDTO evento) {
        publisherService.publicarEvento(evento);
        return ResponseEntity.ok("Evento [" + evento.getTipoEvento() + "] publicado exitosamente para el pedido " + evento.getPedidoId());
    }
}