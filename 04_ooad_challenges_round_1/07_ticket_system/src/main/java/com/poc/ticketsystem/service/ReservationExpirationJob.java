package com.poc.ticketsystem.service;

import com.poc.ticketsystem.model.Seat;
import com.poc.ticketsystem.repository.SeatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReservationExpirationJob {

    private static final Logger log = LoggerFactory.getLogger(ReservationExpirationJob.class);

    private final SeatRepository seatRepository;
    private final Clock clock;

    public ReservationExpirationJob(SeatRepository seatRepository, Clock clock) {
        this.seatRepository = seatRepository;
        this.clock = clock;
    }

    @Scheduled(fixedDelayString = "PT1M", initialDelayString = "PT1M")
    @Transactional
    public int expireHolds() {
        LocalDateTime now = LocalDateTime.now(clock);
        List<Seat> expired = seatRepository.findBySoldFalseAndReservedUntilBefore(now);
        if (expired.isEmpty()) {
            return 0;
        }
        for (Seat s : expired) {
            s.setReservedBy(null);
            s.setReservedUntil(null);
        }
        seatRepository.saveAll(expired);
        log.info("liberou {} assentos com hold expirado", expired.size());
        return expired.size();
    }
}