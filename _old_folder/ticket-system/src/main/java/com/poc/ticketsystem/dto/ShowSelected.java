package com.poc.ticketsystem.dto;

import com.poc.ticketsystem.model.Seat;
import com.poc.ticketsystem.model.Venue;
import com.poc.ticketsystem.model.Zone;


public class ShowSelected {

    private String showName;
    private Venue venue;
    private Zone zone;
    private Seat seat;

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }
}
