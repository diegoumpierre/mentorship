package com.poc.ticketsystem.service;

import com.poc.ticketsystem.dto.ShowSelected;
import com.poc.ticketsystem.model.Show;
import com.poc.ticketsystem.model.User;

import java.util.List;

public class ShowServiceImpl implements ShowService{
    @Override
    public List<Show> listAllShow() {
        return List.of();
    }

    @Override
    public boolean buyTicket(User user, ShowSelected showSelected) {

        //check if still have space to sell a ticket

        //select the seat


        return false;
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
