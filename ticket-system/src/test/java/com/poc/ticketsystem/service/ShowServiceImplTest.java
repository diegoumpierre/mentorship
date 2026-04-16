package com.poc.ticketsystem.service;

import com.poc.ticketsystem.dto.ShowSelected;
import com.poc.ticketsystem.model.Seat;
import com.poc.ticketsystem.model.Show;
import com.poc.ticketsystem.model.User;
import com.poc.ticketsystem.repository.SeatRepository;
import com.poc.ticketsystem.repository.ShowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShowServiceImplTest {

    private static final Instant FIXED_NOW = Instant.parse("2026-04-15T12:00:00Z");

    private SeatRepository seatRepository;
    private ShowRepository showRepository;
    private Clock clock;
    private ShowServiceImpl service;

    @BeforeEach
    void setUp() {
        seatRepository = mock(SeatRepository.class);
        showRepository = mock(ShowRepository.class);
        clock = Clock.fixed(FIXED_NOW, ZoneId.of("UTC"));
        service = new ShowServiceImpl(showRepository, seatRepository, clock);
    }

    private User aUser() {
        User u = new User();
        u.setId(1L);
        u.setName("diego");
        u.setEmail("diego@test.com");
        return u;
    }

    private User anotherUser() {
        User u = new User();
        u.setId(2L);
        u.setName("ana");
        u.setEmail("ana@test.com");
        return u;
    }

    private Seat aSeat(Long id, boolean sold) {
        Seat s = new Seat();
        s.setId(id);
        s.setNumber(10);
        s.setSold(sold);
        return s;
    }

    private ShowSelected withSeat(Seat seat) {
        ShowSelected sel = new ShowSelected();
        sel.setSeat(seat);
        return sel;
    }

    private LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    @Test
    void listAllShow_retornaTudoDoRepo() {
        Show s1 = new Show();
        s1.setId(1L);
        s1.setName("Metallica");
        Show s2 = new Show();
        s2.setId(2L);
        s2.setName("Iron Maiden");
        when(showRepository.findAll()).thenReturn(List.of(s1, s2));

        List<Show> result = service.listAllShow();

        assertEquals(2, result.size());
        assertEquals("Metallica", result.get(0).getName());
        assertEquals("Iron Maiden", result.get(1).getName());
        verify(showRepository).findAll();
    }

    @Test
    void listAllShow_quandoNaoTemNada_retornaVazio() {
        when(showRepository.findAll()).thenReturn(List.of());
        assertTrue(service.listAllShow().isEmpty());
    }

    @Test
    void findById_retornaNull_porEnquanto() {
        assertNull(service.findById("1"));
    }

    @Test
    void buyTicket_compraComSucesso() {
        Seat seat = aSeat(42L, false);
        when(seatRepository.findById(42L)).thenReturn(Optional.of(seat));

        boolean ok = service.buyTicket(aUser(), withSeat(aSeat(42L, false)));

        assertTrue(ok);
        ArgumentCaptor<Seat> captor = ArgumentCaptor.forClass(Seat.class);
        verify(seatRepository).save(captor.capture());
        Seat saved = captor.getValue();
        assertTrue(saved.isSold());
        assertEquals(1L, saved.getUser().getId());
    }

    @Test
    void buyTicket_falhaQuandoAssentoJaVendido() {
        Seat jaVendido = aSeat(7L, true);
        when(seatRepository.findById(7L)).thenReturn(Optional.of(jaVendido));

        boolean ok = service.buyTicket(aUser(), withSeat(aSeat(7L, true)));

        assertFalse(ok);
        verify(seatRepository, never()).save(any());
    }

    @Test
    void buyTicket_falhaQuandoAssentoNaoExiste() {
        when(seatRepository.findById(99L)).thenReturn(Optional.empty());

        boolean ok = service.buyTicket(aUser(), withSeat(aSeat(99L, false)));

        assertFalse(ok);
        verify(seatRepository, never()).save(any());
    }

    @Test
    void buyTicket_falhaQuandoUserNull() {
        assertFalse(service.buyTicket(null, withSeat(aSeat(1L, false))));
        verify(seatRepository, never()).save(any());
    }

    @Test
    void buyTicket_falhaQuandoShowSelectedNull() {
        assertFalse(service.buyTicket(aUser(), null));
        verify(seatRepository, never()).save(any());
    }

    @Test
    void buyTicket_falhaQuandoSeatNull() {
        ShowSelected sel = new ShowSelected();
        assertFalse(service.buyTicket(aUser(), sel));
        verify(seatRepository, never()).save(any());
    }

    @Test
    void buyTicket_falhaQuandoSeatIdNull() {
        Seat semId = new Seat();
        assertFalse(service.buyTicket(aUser(), withSeat(semId)));
        verify(seatRepository, never()).save(any());
    }

    @Test
    void buyTicket_bloqueadoPorReservaAtivaDeOutroUser() {
        Seat seat = aSeat(11L, false);
        seat.setReservedBy(anotherUser());
        seat.setReservedUntil(now().plusMinutes(2));
        when(seatRepository.findById(11L)).thenReturn(Optional.of(seat));

        boolean ok = service.buyTicket(aUser(), withSeat(aSeat(11L, false)));

        assertFalse(ok);
        verify(seatRepository, never()).save(any());
    }

    @Test
    void buyTicket_oUserDaReservaConsegueComprar() {
        Seat seat = aSeat(12L, false);
        seat.setReservedBy(aUser());
        seat.setReservedUntil(now().plusMinutes(2));
        when(seatRepository.findById(12L)).thenReturn(Optional.of(seat));

        boolean ok = service.buyTicket(aUser(), withSeat(aSeat(12L, false)));

        assertTrue(ok);
        ArgumentCaptor<Seat> captor = ArgumentCaptor.forClass(Seat.class);
        verify(seatRepository).save(captor.capture());
        Seat saved = captor.getValue();
        assertTrue(saved.isSold());
        assertNull(saved.getReservedBy());
        assertNull(saved.getReservedUntil());
    }

    @Test
    void buyTicket_reservaExpiradaNaoBloqueia() {
        Seat seat = aSeat(13L, false);
        seat.setReservedBy(anotherUser());
        seat.setReservedUntil(now().minusSeconds(1));
        when(seatRepository.findById(13L)).thenReturn(Optional.of(seat));

        boolean ok = service.buyTicket(aUser(), withSeat(aSeat(13L, false)));

        assertTrue(ok);
    }

    @Test
    void reserveASeat_reservaComSucessoEgravaTTL() {
        Seat seat = aSeat(50L, false);
        when(seatRepository.findById(50L)).thenReturn(Optional.of(seat));

        boolean ok = service.reserveASeat(aUser(), 50L);

        assertTrue(ok);
        ArgumentCaptor<Seat> captor = ArgumentCaptor.forClass(Seat.class);
        verify(seatRepository).save(captor.capture());
        Seat saved = captor.getValue();
        assertEquals(1L, saved.getReservedBy().getId());
        assertNotNull(saved.getReservedUntil());
        assertEquals(now().plus(ShowServiceImpl.RESERVATION_TTL), saved.getReservedUntil());
    }

    @Test
    void reserveASeat_falhaQuandoUserNull() {
        assertFalse(service.reserveASeat(null, 1L));
        verify(seatRepository, never()).save(any());
    }

    @Test
    void reserveASeat_falhaQuandoSeatIdNull() {
        assertFalse(service.reserveASeat(aUser(), null));
        verify(seatRepository, never()).save(any());
    }

    @Test
    void reserveASeat_falhaQuandoSeatNaoExiste() {
        when(seatRepository.findById(404L)).thenReturn(Optional.empty());
        assertFalse(service.reserveASeat(aUser(), 404L));
        verify(seatRepository, never()).save(any());
    }

    @Test
    void reserveASeat_falhaQuandoJaVendido() {
        Seat vendido = aSeat(60L, true);
        when(seatRepository.findById(60L)).thenReturn(Optional.of(vendido));

        assertFalse(service.reserveASeat(aUser(), 60L));
        verify(seatRepository, never()).save(any());
    }

    @Test
    void reserveASeat_falhaQuandoReservadoPorOutro() {
        Seat seat = aSeat(61L, false);
        seat.setReservedBy(anotherUser());
        seat.setReservedUntil(now().plusMinutes(3));
        when(seatRepository.findById(61L)).thenReturn(Optional.of(seat));

        assertFalse(service.reserveASeat(aUser(), 61L));
        verify(seatRepository, never()).save(any());
    }

    @Test
    void reserveASeat_permiteRenovarQuandoOMesmoUserJaTinhaReservado() {
        Seat seat = aSeat(62L, false);
        seat.setReservedBy(aUser());
        seat.setReservedUntil(now().plusMinutes(1));
        when(seatRepository.findById(62L)).thenReturn(Optional.of(seat));

        boolean ok = service.reserveASeat(aUser(), 62L);

        assertTrue(ok);
        ArgumentCaptor<Seat> captor = ArgumentCaptor.forClass(Seat.class);
        verify(seatRepository).save(captor.capture());
        assertEquals(now().plus(ShowServiceImpl.RESERVATION_TTL), captor.getValue().getReservedUntil());
    }

    @Test
    void reserveASeat_permiteReservarSeReservaAntigaExpirou() {
        Seat seat = aSeat(63L, false);
        seat.setReservedBy(anotherUser());
        seat.setReservedUntil(now().minusMinutes(1));
        when(seatRepository.findById(63L)).thenReturn(Optional.of(seat));

        boolean ok = service.reserveASeat(aUser(), 63L);

        assertTrue(ok);
        ArgumentCaptor<Seat> captor = ArgumentCaptor.forClass(Seat.class);
        verify(seatRepository).save(captor.capture());
        assertEquals(1L, captor.getValue().getReservedBy().getId());
    }

    @Test
    void buyTicket_falhaComOptimisticLock() {
        Seat seat = aSeat(70L, false);
        when(seatRepository.findById(70L)).thenReturn(Optional.of(seat));
        when(seatRepository.save(any())).thenThrow(new ObjectOptimisticLockingFailureException(Seat.class, 70L));

        boolean ok = service.buyTicket(aUser(), withSeat(aSeat(70L, false)));

        assertFalse(ok);
    }

    @Test
    void reserveASeat_falhaComOptimisticLock() {
        Seat seat = aSeat(71L, false);
        when(seatRepository.findById(71L)).thenReturn(Optional.of(seat));
        when(seatRepository.save(any())).thenThrow(new ObjectOptimisticLockingFailureException(Seat.class, 71L));

        boolean ok = service.reserveASeat(aUser(), 71L);

        assertFalse(ok);
    }
}
