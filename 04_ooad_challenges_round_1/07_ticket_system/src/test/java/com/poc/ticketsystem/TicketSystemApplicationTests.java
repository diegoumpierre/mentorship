package com.poc.ticketsystem;

import com.poc.ticketsystem.model.Show;
import com.poc.ticketsystem.repository.SeatRepository;
import com.poc.ticketsystem.repository.ShowRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class TicketSystemApplicationTests {

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void liquibaseSeed_carregouShows() {
        List<Show> shows = showRepository.findAll();
        assertEquals(2, shows.size());
    }

    @Test
    void liquibaseSeed_carregouSeats() {
        assertTrue(seatRepository.count() >= 4);
    }
}
