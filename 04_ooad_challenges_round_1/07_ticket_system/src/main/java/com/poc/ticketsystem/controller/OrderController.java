package com.poc.ticketsystem.controller;

import com.poc.ticketsystem.service.ShowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final ShowService showService;

    public OrderController(ShowService showService) {
        this.showService = showService;
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancel(@PathVariable Long orderId) {
        if (showService.cancelOrder(orderId)) {
            return ResponseEntity.ok("cancelled");
        }
        return ResponseEntity.status(409).body("cannot cancel");
    }
}
