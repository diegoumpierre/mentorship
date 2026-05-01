package com.poc.ticketsystem.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SeatMapIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void seatMap_seedSemReservas_mostraSoldEAvailable() throws Exception {
        // show_date 1: seats 1,2,3 livres, seat 4 ja vendido no seed
        mvc.perform(get("/shows/dates/1/seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].status").value("AVAILABLE"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].status").value("AVAILABLE"))
                .andExpect(jsonPath("$[3].id").value(4))
                .andExpect(jsonPath("$[3].status").value("SOLD"));
    }

    @Test
    void seatMap_apontaHeldQuandoTemReservaAtiva() throws Exception {
        mvc.perform(post("/shows/seats/2/reserve").param("userId", "1"))
                .andExpect(status().isOk());

        mvc.perform(get("/shows/dates/1/seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].status").value("HELD"));
    }

    @Test
    void seatMap_traduzNomeDaZone() throws Exception {
        mvc.perform(get("/shows/dates/1/seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].zoneName").value("Pista"))
                .andExpect(jsonPath("$[2].zoneName").value("Cadeira Superior"));
    }

    @Test
    void seatMap_showDateInexistente_retornaListaVazia() throws Exception {
        mvc.perform(get("/shows/dates/999/seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
