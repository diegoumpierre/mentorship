package com.poc.ticketsystem.service;

import com.poc.ticketsystem.dto.ShowSelected;
import com.poc.ticketsystem.model.Seat;
import com.poc.ticketsystem.model.User;
import com.poc.ticketsystem.repository.SeatRepository;
import com.poc.ticketsystem.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ConcurrencyLoadTest {

    private static final int THREADS = 50;

    @Autowired
    private ShowService showService;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void optimisticLockGaranteUmCompradorPorAssento() throws InterruptedException {
        Long seatId = 1L;
        List<User> users = userRepository.findAll();

        Result r = runLoad(i -> {
            User user = users.get(i % users.size());
            return showService.buyTicket(user, sel(seatId));
        });

        Seat after = seatRepository.findById(seatId).orElseThrow();
        String winner = after.getUser() != null ? after.getUser().getName() : "?";

        System.out.printf(
            "[optimistic-lock] threads=%d sucessos=%d falhas=%d vencedor=%s%n",
            THREADS, r.success, r.fail, winner);

        assertEquals(1, r.success, "apenas 1 thread devia conseguir comprar o mesmo assento");
        assertEquals(THREADS - 1, r.fail);
        assertTrue(after.isSold());
    }

    @Test
    void reservaAtivaBloqueiaOutrosCompradores() throws InterruptedException {
        Long seatId = 2L;
        User diego = userRepository.findById(1L).orElseThrow();
        User ana = userRepository.findById(2L).orElseThrow();

        assertTrue(showService.reserveASeat(diego, seatId), "Diego devia conseguir reservar");

        Result r = runLoad(i -> showService.buyTicket(ana, sel(seatId)));

        System.out.printf(
            "[reservation-ttl] threads=%d sucessos=%d falhas=%d%n",
            THREADS, r.success, r.fail);

        assertEquals(0, r.success, "ninguem alem de quem reservou devia comprar enquanto a reserva esta ativa");
        assertEquals(THREADS, r.fail);

        assertTrue(showService.buyTicket(diego, sel(seatId)),
            "quem reservou ainda deve conseguir fechar a compra");
    }

    @Test
    void capacidadeDoShowDateEAssentoLimitamAsVendas() throws InterruptedException {
        // show_date 1: capacity=4, seat 4 ja vendido no seed -> 3 livres (seats 1,2,3).
        // Cada thread escolhe um dos 3 -> max 1 venda por seat (@Version) -> 3 vendas no total.
        List<Long> seatIds = List.of(1L, 2L, 3L);
        List<User> users = userRepository.findAll();

        Result r = runLoad(i -> {
            Long seatId = seatIds.get(i % seatIds.size());
            User user = users.get(i % users.size());
            return showService.buyTicket(user, sel(seatId));
        });

        long soldNoShowDate = seatRepository.countByShowDateIdAndSoldTrue(1L);

        System.out.printf(
            "[capacity-check] threads=%d sucessos=%d falhas=%d soldNoShowDate=%d/4%n",
            THREADS, r.success, r.fail, soldNoShowDate);

        assertEquals(3, r.success, "1 venda por seat livre");
        assertEquals(4, soldNoShowDate, "show_date deve estar lotado (3 vendidos agora + 1 do seed)");
    }

    private Result runLoad(Attempt attempt) throws InterruptedException {
        CountDownLatch ready = new CountDownLatch(THREADS);
        CountDownLatch go = new CountDownLatch(1);
        AtomicInteger success = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();

        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        for (int i = 0; i < THREADS; i++) {
            int idx = i;
            pool.submit(() -> {
                ready.countDown();
                try {
                    go.await();
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    return;
                }
                try {
                    if (attempt.run(idx)) {
                        success.incrementAndGet();
                    } else {
                        fail.incrementAndGet();
                    }
                } catch (RuntimeException e) {
                    fail.incrementAndGet();
                }
            });
        }

        ready.await();
        go.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(30, TimeUnit.SECONDS), "pool nao terminou no tempo");

        Result r = new Result();
        r.success = success.get();
        r.fail = fail.get();
        return r;
    }

    private ShowSelected sel(Long seatId) {
        ShowSelected sel = new ShowSelected();
        Seat ref = new Seat();
        ref.setId(seatId);
        sel.setSeat(ref);
        return sel;
    }

    private interface Attempt {
        boolean run(int index);
    }

    private static class Result {
        int success;
        int fail;
    }
}