package com.pedidos360.orders.controller;

import com.pedidos360.orders.dto.CrearColaDTO;
import com.pedidos360.orders.service.RabbitAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final RabbitAdminService adminService;

    public AdminController(RabbitAdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/queues")
    public ResponseEntity<String> crearCola(@RequestBody CrearColaDTO dto) {
        if (dto.getNombreCola() == null || dto.getNombreCola().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre de la cola no puede estar vacío.");
        }
        adminService.crearColaDurable(dto.getNombreCola());
        return ResponseEntity.ok("Cola '" + dto.getNombreCola() + "' creada exitosamente.");
    }

    @DeleteMapping("/queues/{nombre}")
    public ResponseEntity<String> eliminarCola(@PathVariable String nombre) {
        boolean eliminada = adminService.eliminarCola(nombre);
        if (eliminada) {
            return ResponseEntity.ok("Cola '" + nombre + "' eliminada correctamente.");
        } else {
            return ResponseEntity.badRequest().body("No se pudo eliminar la cola o no existía.");
        }
    }
}