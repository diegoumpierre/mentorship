package com.poc.ticketsystem.controller;

import com.poc.ticketsystem.model.Order;
import com.poc.ticketsystem.model.Seat;
import com.poc.ticketsystem.repository.OrderRepository;
import com.poc.ticketsystem.repository.SeatRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Test
    void cancelOrder_liberaOAssentoEMudaStatus() throws Exception {
        mvc.perform(post("/shows/buy")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seat\":{\"id\":3}}"))
                .andExpect(status().isOk());

        Long orderId = ultimoOrderId();

        mvc.perform(post("/orders/" + orderId + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(content().string("cancelled"));

        Seat seat = seatRepository.findById(3L).orElseThrow();
        assertFalse(seat.isSold(), "assento devia voltar a ser available");
        assertNull(seat.getUser());

        Order order = orderRepository.findById(orderId).orElseThrow();
        assertEquals("CANCELLED", order.getStatus());
    }

    @Test
    void cancelOrder_segundoCancelamentoFalha() throws Exception {
        mvc.perform(post("/shows/buy")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seat\":{\"id\":1}}"))
                .andExpect(status().isOk());

        Long orderId = ultimoOrderId();

        mvc.perform(post("/orders/" + orderId + "/cancel"))
                .andExpect(status().isOk());

        mvc.perform(post("/orders/" + orderId + "/cancel"))
                .andExpect(status().isConflict())
                .andExpect(content().string("cannot cancel"));
    }

    @Test
    void cancelOrder_orderInexistente() throws Exception {
        mvc.perform(post("/orders/9999/cancel"))
                .andExpect(status().isConflict());
    }

    private Long ultimoOrderId() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(Order::getId)
                .max(Comparator.naturalOrder())
                .orElseThrow();
    }
}
