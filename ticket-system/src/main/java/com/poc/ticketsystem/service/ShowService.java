package com.poc.ticketsystem.service;

import com.poc.ticketsystem.dto.ShowSelected;
import com.poc.ticketsystem.model.Show;
import com.poc.ticketsystem.model.User;

import java.util.List;

public interface ShowService {

    List<Show> listAllShow();

    boolean buyTicket(User user, ShowSelected showSelected);


    boolean reserveASeat();

    Show findById(String id);

}
