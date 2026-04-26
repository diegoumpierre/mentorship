package com.poc.ticketsystem.controller;

import com.poc.ticketsystem.repository.SeatRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ShowControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private SeatRepository seatRepository;

    @Test
    void listShows_retornaSeedData() throws Exception {
        mvc.perform(get("/shows"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Metallica - M72 Tour"))
                .andExpect(jsonPath("$[1].name").value("Iron Maiden - Future Past"));
    }

    @Test
    void reserveSeat_comSucesso() throws Exception {
        mvc.perform(post("/shows/seats/2/reserve").param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("reserved"));
    }

    @Test
    void reserveSeat_userInexistente() throws Exception {
        mvc.perform(post("/shows/seats/2/reserve").param("userId", "999"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buySeat_comSucesso() throws Exception {
        mvc.perform(post("/shows/buy")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seat\":{\"id\":3}}"))
                .andExpect(status().isOk())
                .andExpect(content().string("sold"));
    }

    @Test
    void buySeat_assentoJaVendido() throws Exception {
        mvc.perform(post("/shows/buy")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seat\":{\"id\":4}}"))
                .andExpect(status().isConflict());
    }

    @Test
    void buySeat_userInexistente() throws Exception {
        mvc.perform(post("/shows/buy")
                        .param("userId", "999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seat\":{\"id\":1}}"))
                .andExpect(status().isBadRequest());
    }
}
