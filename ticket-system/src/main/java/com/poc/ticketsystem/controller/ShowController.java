package com.poc.ticketsystem.controller;

import com.poc.ticketsystem.dto.ShowSelected;
import com.poc.ticketsystem.model.Show;
import com.poc.ticketsystem.model.User;
import com.poc.ticketsystem.repository.UserRepository;
import com.poc.ticketsystem.service.ShowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shows")
public class ShowController {

    private final ShowService showService;
    private final UserRepository userRepository;

    public ShowController(ShowService showService, UserRepository userRepository) {
        this.showService = showService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<Show>> listAll() {
        return ResponseEntity.ok(showService.listAllShow());
    }

    @PostMapping("/seats/{seatId}/reserve")
    public ResponseEntity<String> reserveSeat(@PathVariable Long seatId, @RequestParam Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("user not found");
        }
        boolean ok = showService.reserveASeat(user, seatId);
        if (!ok) {
            return ResponseEntity.status(409).body("seat not available");
        }
        return ResponseEntity.ok("reserved");
    }

    @PostMapping("/buy")
    public ResponseEntity<String> buy(@RequestParam Long userId, @RequestBody ShowSelected showSelected) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("user not found");
        }
        boolean ok = showService.buyTicket(user, showSelected);
        if (!ok) {
            return ResponseEntity.status(409).body("seat not available");
        }
        return ResponseEntity.ok("sold");
    }
}
