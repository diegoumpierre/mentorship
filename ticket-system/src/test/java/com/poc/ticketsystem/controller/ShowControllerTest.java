package com.poc.ticketsystem.controller;

import com.poc.ticketsystem.dto.ShowSelected;
import com.poc.ticketsystem.model.Seat;
import com.poc.ticketsystem.model.Show;
import com.poc.ticketsystem.model.User;
import com.poc.ticketsystem.repository.UserRepository;
import com.poc.ticketsystem.service.ShowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShowControllerTest {

    private ShowService showService;
    private UserRepository userRepository;
    private ShowController controller;

    @BeforeEach
    void setUp() {
        showService = mock(ShowService.class);
        userRepository = mock(UserRepository.class);
        controller = new ShowController(showService, userRepository);
    }

    private User aUser() {
        User u = new User();
        u.setId(1L);
        u.setName("diego");
        return u;
    }

    @Test
    void listAll_retornaOsShows() {
        Show s = new Show();
        s.setId(1L);
        s.setName("Metallica");
        when(showService.listAllShow()).thenReturn(List.of(s));

        ResponseEntity<List<Show>> resp = controller.listAll();

        assertEquals(200, resp.getStatusCode().value());
        assertEquals(1, resp.getBody().size());
        assertEquals("Metallica", resp.getBody().get(0).getName());
    }

    @Test
    void reserveSeat_okQuandoServicoRetornaTrue() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(aUser()));
        when(showService.reserveASeat(any(), any())).thenReturn(true);

        ResponseEntity<String> resp = controller.reserveSeat(10L, 1L);

        assertEquals(200, resp.getStatusCode().value());
        assertEquals("reserved", resp.getBody());
    }

    @Test
    void reserveSeat_conflitoQuandoServicoRetornaFalse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(aUser()));
        when(showService.reserveASeat(any(), any())).thenReturn(false);

        ResponseEntity<String> resp = controller.reserveSeat(10L, 1L);

        assertEquals(409, resp.getStatusCode().value());
    }

    @Test
    void reserveSeat_badRequestQuandoUserNaoExiste() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<String> resp = controller.reserveSeat(10L, 99L);

        assertEquals(400, resp.getStatusCode().value());
        verify(showService, never()).reserveASeat(any(), any());
    }

    @Test
    void buy_okQuandoServicoRetornaTrue() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(aUser()));
        when(showService.buyTicket(any(), any())).thenReturn(true);

        ShowSelected sel = new ShowSelected();
        Seat seat = new Seat();
        seat.setId(10L);
        sel.setSeat(seat);

        ResponseEntity<String> resp = controller.buy(1L, sel);

        assertEquals(200, resp.getStatusCode().value());
        assertEquals("sold", resp.getBody());
    }

    @Test
    void buy_conflitoQuandoServicoRetornaFalse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(aUser()));
        when(showService.buyTicket(any(), any())).thenReturn(false);

        ResponseEntity<String> resp = controller.buy(1L, new ShowSelected());

        assertEquals(409, resp.getStatusCode().value());
    }

    @Test
    void buy_badRequestQuandoUserNaoExiste() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<String> resp = controller.buy(99L, new ShowSelected());

        assertEquals(400, resp.getStatusCode().value());
        verify(showService, never()).buyTicket(any(), any());
    }
}
