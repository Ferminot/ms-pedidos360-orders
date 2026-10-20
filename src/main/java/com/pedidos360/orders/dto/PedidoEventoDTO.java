package com.pedidos360.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoEventoDTO implements Serializable {
    private String pedidoId;
    private String tipoEvento; // "PedidoCreado", "PedidoAceptado", "PedidoDespachado"
    private String cliente;
    private String detalle;
    private boolean simularError; // Util para pruebas de DLQ
}