package com.poc.ticketsystem.service;

import com.poc.ticketsystem.dto.ShowSelected;
import com.poc.ticketsystem.model.Seat;
import com.poc.ticketsystem.model.Show;
import com.poc.ticketsystem.model.User;
import com.poc.ticketsystem.repository.SeatRepository;
import com.poc.ticketsystem.repository.ShowRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShowServiceImpl implements ShowService {

    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;

    public ShowServiceImpl(ShowRepository showRepository, SeatRepository seatRepository) {
        this.showRepository = showRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public List<Show> listAllShow() {
        return showRepository.findAll();
    }

    @Override
    public boolean buyTicket(User user, ShowSelected showSelected) {
        if (user == null || showSelected == null || showSelected.getSeat() == null) {
            return false;
        }

        Seat picked = showSelected.getSeat();
        if (picked.getId() == null) {
            return false;
        }

        // pega o assento do banco pra não confiar no que veio do request
        Seat seat = seatRepository.findById(picked.getId()).orElse(null);
        if (seat == null) {
            return false;
        }

        // alguem já comprou
        if (seat.isSold()) {
            return false;
        }

        seat.setSold(true);
        seat.setUser(user);
        seatRepository.save(seat);
        return true;
    }

    @Override
    public boolean reserveASeat() {
        return false;
    }

    @Override
    public Show findById(String id) {
        return null;
    }
}
