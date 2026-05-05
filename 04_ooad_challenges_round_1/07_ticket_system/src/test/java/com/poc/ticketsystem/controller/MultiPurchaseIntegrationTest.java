package com.poc.ticketsystem.controller;

import com.poc.ticketsystem.model.Order;
import com.poc.ticketsystem.model.Seat;
import com.poc.ticketsystem.repository.OrderRepository;
import com.poc.ticketsystem.repository.SeatRepository;
import com.poc.ticketsystem.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MultiPurchaseIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void buyMany_compraVariosNumaTransacao() throws Exception {
        // seats 1 e 2 estao na zone Pista (preco 250 cada)
        long ordersAntes = orderRepository.count();
        long ticketsAntes = ticketRepository.count();

        mvc.perform(post("/shows/buy-many")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seatIds\":[1,2]}"))
                .andExpect(status().isOk())
                .andExpect(content().string("sold"));

        assertTrue(seatRepository.findById(1L).orElseThrow().isSold());
        assertTrue(seatRepository.findById(2L).orElseThrow().isSold());

        assertEquals(ordersAntes + 1, orderRepository.count());
        assertEquals(ticketsAntes + 2, ticketRepository.count());

        Order order = ultimaOrder();
        assertEquals(0, order.getTotal().compareTo(new BigDecimal("500.00")));
        assertEquals("PAID", order.getStatus());
    }

    @Test
    void buyMany_assentoJaVendidoNoMeio_fazRollback() throws Exception {
        // seat 4 ja vem vendido no seed; comprar [1, 4] deve falhar e seat 1 NAO pode ficar vendido
        long ordersAntes = orderRepository.count();
        long ticketsAntes = ticketRepository.count();

        mvc.perform(post("/shows/buy-many")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seatIds\":[1,4]}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("purchase failed"));

        Seat seat1 = seatRepository.findById(1L).orElseThrow();
        assertFalse(seat1.isSold(), "rollback devia deixar o seat 1 livre");
        assertNull(seat1.getUser());

        assertEquals(ordersAntes, orderRepository.count(), "nenhuma order nova");
        assertEquals(ticketsAntes, ticketRepository.count(), "nenhum ticket novo");
    }

    @Test
    void buyMany_seatInexistente_fazRollback() throws Exception {
        long ordersAntes = orderRepository.count();

        mvc.perform(post("/shows/buy-many")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seatIds\":[2,9999]}"))
                .andExpect(status().isConflict());

        assertFalse(seatRepository.findById(2L).orElseThrow().isSold());
        assertEquals(ordersAntes, orderRepository.count());
    }

    @Test
    void buyMany_listaVazia() throws Exception {
        mvc.perform(post("/shows/buy-many")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seatIds\":[]}"))
                .andExpect(status().isConflict());
    }

    @Test
    void buyMany_userInexistente() throws Exception {
        mvc.perform(post("/shows/buy-many")
                        .param("userId", "9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seatIds\":[1,2]}"))
                .andExpect(status().isBadRequest());
    }

    private Order ultimaOrder() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .max(Comparator.comparing(Order::getId))
                .orElseThrow();
    }
}
