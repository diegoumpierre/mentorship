package com.poc.ticketsystem.service;

import com.poc.ticketsystem.model.Seat;
import com.poc.ticketsystem.model.User;
import com.poc.ticketsystem.repository.SeatRepository;
import com.poc.ticketsystem.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationExpirationJobTest {

    @Autowired
    private ReservationExpirationJob job;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void liberaAssentosComHoldExpirado() {
        Seat seat = seatRepository.findById(1L).orElseThrow();
        User diego = userRepository.findById(1L).orElseThrow();
        seat.setReservedBy(diego);
        seat.setReservedUntil(LocalDateTime.now().minusMinutes(10));
        seatRepository.save(seat);

        int liberados = job.expireHolds();

        assertEquals(1, liberados);
        Seat depois = seatRepository.findById(1L).orElseThrow();
        assertNull(depois.getReservedBy());
        assertNull(depois.getReservedUntil());
    }

    @Test
    void naoMexeEmHoldsAindaValidos() {
        Seat seat = seatRepository.findById(2L).orElseThrow();
        User ana = userRepository.findById(2L).orElseThrow();
        seat.setReservedBy(ana);
        seat.setReservedUntil(LocalDateTime.now().plusMinutes(5));
        seatRepository.save(seat);

        int liberados = job.expireHolds();

        assertEquals(0, liberados);
        Seat depois = seatRepository.findById(2L).orElseThrow();
        assertNotNull(depois.getReservedBy());
        assertNotNull(depois.getReservedUntil());
    }

    @Test
    void ignoraAssentosJaVendidosMesmoComReservedUntilAntigo() {
        // seat 4 ja vem vendido no seed; mesmo com reserva antiga nao entra na varredura
        Seat seat = seatRepository.findById(4L).orElseThrow();
        User ana = userRepository.findById(2L).orElseThrow();
        seat.setReservedBy(ana);
        seat.setReservedUntil(LocalDateTime.now().minusHours(1));
        seatRepository.save(seat);

        int liberados = job.expireHolds();

        assertEquals(0, liberados);
    }
}