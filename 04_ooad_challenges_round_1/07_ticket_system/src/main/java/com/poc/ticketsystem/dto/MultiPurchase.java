package com.poc.ticketsystem.dto;

import java.util.List;

public class MultiPurchase {

    private List<Long> seatIds;

    public List<Long> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<Long> seatIds) {
        this.seatIds = seatIds;
    }
}
