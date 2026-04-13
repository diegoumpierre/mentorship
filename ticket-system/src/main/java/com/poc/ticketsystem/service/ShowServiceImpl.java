package com.poc.ticketsystem.service;

import com.poc.ticketsystem.dto.ShowSelected;
import com.poc.ticketsystem.model.Seat;
import com.poc.ticketsystem.model.Show;
import com.poc.ticketsystem.model.User;
import com.poc.ticketsystem.repository.SeatRepository;
import com.poc.ticketsystem.repository.ShowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShowServiceImpl implements ShowService {

    static final Duration RESERVATION_TTL = Duration.ofMinutes(5);

    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final Clock clock;

    public ShowServiceImpl(ShowRepository showRepository, SeatRepository seatRepository, Clock clock) {
        this.showRepository = showRepository;
        this.seatRepository = seatRepository;
        this.clock = clock;
    }

    @Override
    public List<Show> listAllShow() {
        return showRepository.findAll();
    }

    @Override
    @Transactional
    public boolean buyTicket(User user, ShowSelected showSelected) {
        if (user == null || showSelected == null || showSelected.getSeat() == null) {
            return false;
        }

        Seat picked = showSelected.getSeat();
        if (picked.getId() == null) {
            return false;
        }

        // pega o assento do banco pra não confiar no que veio do request
        Seat seat = seatRepository.findByIdForUpdate(picked.getId()).orElse(null);
        if (seat == null) {
            return false;
        }

        // alguem já comprou
        if (seat.isSold()) {
            return false;
        }

        if (hasActiveReservationBySomeoneElse(seat, user)) {
            return false;
        }

        seat.setSold(true);
        seat.setUser(user);
        seat.setReservedBy(null);
        seat.setReservedUntil(null);
        seatRepository.save(seat);
        return true;
    }

    @Override
    @Transactional
    public boolean reserveASeat(User user, Long seatId) {
        if (user == null || user.getId() == null || seatId == null) {
            return false;
        }

        Seat seat = seatRepository.findByIdForUpdate(seatId).orElse(null);
        if (seat == null || seat.isSold()) {
            return false;
        }

        if (hasActiveReservationBySomeoneElse(seat, user)) {
            return false;
        }

        seat.setReservedBy(user);
        seat.setReservedUntil(LocalDateTime.now(clock).plus(RESERVATION_TTL));
        seatRepository.save(seat);
        return true;
    }

    @Override
    public Show findById(String id) {
        return null;
    }

    private boolean hasActiveReservationBySomeoneElse(Seat seat, User user) {
        User holder = seat.getReservedBy();
        LocalDateTime until = seat.getReservedUntil();
        if (holder == null || until == null) {
            return false;
        }
        if (until.isBefore(LocalDateTime.now(clock))) {
            return false;
        }
        return !holder.getId().equals(user.getId());
    }
}
