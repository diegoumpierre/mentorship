package com.poc.ticketsystem.service;

import com.poc.ticketsystem.dto.ShowSelected;
import com.poc.ticketsystem.model.Seat;
import com.poc.ticketsystem.model.User;
import com.poc.ticketsystem.repository.SeatRepository;
import com.poc.ticketsystem.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ConcurrencyBuyTest {

    @Autowired
    private ShowService showService;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void soDeixaUmComprarOMesmoAssento() throws InterruptedException {
        Seat seat = seatRepository.findById(1L).orElseThrow();
        User diego = userRepository.findById(1L).orElseThrow();
        User ana = userRepository.findById(2L).orElseThrow();

        int threads = 2;
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch go = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);

        ExecutorService pool = Executors.newFixedThreadPool(threads);

        pool.submit(() -> {
            ready.countDown();
            try { go.await(); } catch (InterruptedException ignored) {}
            ShowSelected sel = new ShowSelected();
            Seat ref = new Seat();
            ref.setId(seat.getId());
            sel.setSeat(ref);
            if (showService.buyTicket(diego, sel)) {
                successCount.incrementAndGet();
            }
        });

        pool.submit(() -> {
            ready.countDown();
            try { go.await(); } catch (InterruptedException ignored) {}
            ShowSelected sel = new ShowSelected();
            Seat ref = new Seat();
            ref.setId(seat.getId());
            sel.setSeat(ref);
            if (showService.buyTicket(ana, sel)) {
                successCount.incrementAndGet();
            }
        });

        ready.await();
        go.countDown();
        pool.shutdown();
        pool.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);

        assertTrue(successCount.get() <= 1);

        Seat after = seatRepository.findById(1L).orElseThrow();
        assertTrue(after.isSold());
    }
}
