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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShowServiceImplTest {

    private SeatRepository seatRepository;
    private ShowRepository showRepository;
    private ShowServiceImpl service;

    @BeforeEach
    void setUp() {
        seatRepository = mock(SeatRepository.class);
        showRepository = mock(ShowRepository.class);
        service = new ShowServiceImpl(showRepository, seatRepository);
    }

    private User aUser() {
        User u = new User();
        u.setId(1L);
        u.setName("diego");
        u.setEmail("diego@test.com");
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
    void reserveASeat_aindaNaoImplementado() {
        assertFalse(service.reserveASeat());
    }

    @Test
    void findById_retornaNull_porEnquanto() {
        assertEquals(null, service.findById("1"));
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
}
