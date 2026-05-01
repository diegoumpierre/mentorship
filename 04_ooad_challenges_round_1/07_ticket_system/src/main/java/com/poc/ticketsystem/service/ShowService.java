package com.poc.ticketsystem.service;

import com.poc.ticketsystem.dto.SeatStatusView;
import com.poc.ticketsystem.dto.ShowSelected;
import com.poc.ticketsystem.model.Show;
import com.poc.ticketsystem.model.User;

import java.util.List;

public interface ShowService {

    List<Show> listAllShow();

    boolean buyTicket(User user, ShowSelected showSelected);


    boolean reserveASeat(User user, Long seatId);

    Show findById(String id);

    List<SeatStatusView> seatMapByShowDate(Long showDateId);

    boolean cancelOrder(Long orderId);

}
