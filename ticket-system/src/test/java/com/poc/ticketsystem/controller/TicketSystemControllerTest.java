package com.poc.ticketsystem.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TicketSystemControllerTest {

    @Test
    void root_deveRetornarWelcome() {
        TicketSystemController controller = new TicketSystemController();
        ResponseEntity<String> resp = controller.findById();
        assertEquals(200, resp.getStatusCode().value());
        assertEquals("welcome", resp.getBody());
    }
}
