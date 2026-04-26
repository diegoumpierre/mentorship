package com.poc.ticketsystem.repository;

import com.poc.ticketsystem.model.Order;
import com.poc.ticketsystem.model.Seat;
import com.poc.ticketsystem.model.Show;
import com.poc.ticketsystem.model.ShowDate;
import com.poc.ticketsystem.model.Ticket;
import com.poc.ticketsystem.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class DomainPersistenceTest {

    @Autowired
    private ShowDateRepository showDateRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ShowRepository showRepository;

    @Test
    void salvaShowDateELeDeVolta() {
        Show show = showRepository.findById(1L).orElseThrow();

        ShowDate sd = new ShowDate();
        sd.setShow(show);
        sd.setDate(LocalDateTime.of(2026, 7, 1, 21, 0));
        sd.setCapacity(120);
        ShowDate saved = showDateRepository.save(sd);

        assertNotNull(saved.getId());
        ShowDate back = showDateRepository.findById(saved.getId()).orElseThrow();
        assertEquals(120, back.getCapacity());
        assertEquals(1L, back.getShow().getId());
    }

    @Test
    void buscaShowDatesPorShowId() {
        List<ShowDate> doShow = showDateRepository.findByShowId(1L);
        assertEquals(1, doShow.size());
        assertEquals(4, doShow.get(0).getCapacity());
    }

    @Test
    void salvaOrderComStatusETotal() {
        User user = userRepository.findById(1L).orElseThrow();

        Order order = new Order();
        order.setUser(user);
        order.setStatus("PAID");
        order.setTotal(new BigDecimal("180.00"));
        order.setCreatedAt(LocalDateTime.now());

        Order saved = orderRepository.save(order);
        assertNotNull(saved.getId());

        Order back = orderRepository.findById(saved.getId()).orElseThrow();
        assertEquals("PAID", back.getStatus());
        assertEquals(0, new BigDecimal("180.00").compareTo(back.getTotal()));
        assertEquals(1L, back.getUser().getId());
    }

    @Test
    void salvaTicketLigadoNoOrderEnoSeat() {
        User user = userRepository.findById(1L).orElseThrow();

        Order order = new Order();
        order.setUser(user);
        order.setStatus("PAID");
        order.setTotal(new BigDecimal("250.00"));
        order.setCreatedAt(LocalDateTime.now());
        order = orderRepository.save(order);

        Seat seat = seatRepository.findById(1L).orElseThrow();

        Ticket ticket = new Ticket();
        ticket.setOrder(order);
        ticket.setSeat(seat);
        ticket.setPrice(new BigDecimal("250.00"));
        Ticket saved = ticketRepository.save(ticket);

        Ticket back = ticketRepository.findById(saved.getId()).orElseThrow();
        assertEquals(order.getId(), back.getOrder().getId());
        assertEquals(seat.getId(), back.getSeat().getId());
        assertEquals(0, new BigDecimal("250.00").compareTo(back.getPrice()));
    }
}
