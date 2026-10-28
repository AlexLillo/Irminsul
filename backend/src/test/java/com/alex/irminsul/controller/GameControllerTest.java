package com.alex.irminsul.controller;

import com.alex.irminsul.dto.GameRequestDTO;
import com.alex.irminsul.dto.GameResponseDTO;
import com.alex.irminsul.entity.Genre;
import com.alex.irminsul.exception.ResourceInUseException;
import com.alex.irminsul.service.GameService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
@AutoConfigureMockMvc(addFilters = false)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService gameService;

    @Test
    @DisplayName("GET /api/games - Should return 200 OK and games list")
    void findAll_ShouldReturnGamesList_WhenCalled() throws Exception {
        GameResponseDTO game = new GameResponseDTO(
                1L,
                "Elden Ring",
                "Action RPG",
                LocalDate.of(2022, 2, 25),
                "https://example.com/cover.jpg",
                Genre.RPG,
                Collections.emptySet()
        );

        when(gameService.findAll()).thenReturn(List.of(game));

        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Elden Ring"))
                .andExpect(jsonPath("$[0].genre").value("RPG"));
    }

    @Test
    @DisplayName("GET /api/games/{id} - Should return 200 OK when game exists")
    void findById_ShouldReturnGame_WhenExists() throws Exception {
        GameResponseDTO game = new GameResponseDTO(
                1L,
                "Hollow Knight",
                "Metroidvania",
                LocalDate.of(2017, 2, 24),
                null,
                Genre.PLATFORMER,
                Collections.emptySet()
        );

        when(gameService.findById(1L)).thenReturn(Optional.of(game));

        mockMvc.perform(get("/api/games/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Hollow Knight"));
    }

    @Test
    @DisplayName("GET /api/games/{id} - Should return 404 Not Found when game does not exist")
    void findById_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        when(gameService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/games/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/games - Should return 400 Bad Request when title is blank")
    void save_ShouldReturnBadRequest_WhenTitleIsBlank() throws Exception {
        String invalidPayload = """
                {
                    "title": "",
                    "description": "Description",
                    "releaseDate": null,
                    "coverUrl": null,
                    "genre": "ACTION",
                    "platformIds": []
                }
                """;

        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").exists());
    }

    @Test
    @DisplayName("POST /api/games - Should return 201 Created and saved game when request is valid")
    void save_ShouldReturnSavedGame_WhenValid() throws Exception {
        String validPayload = """
                {
                    "title": "Bloodborne",
                    "description": "Gothic action RPG",
                    "releaseDate": "2015-03-24",
                    "coverUrl": "https://example.com/bb.jpg",
                    "genre": "RPG",
                    "platformIds": [1]
                }
                """;

        GameResponseDTO responseDTO = new GameResponseDTO(
                5L,
                "Bloodborne",
                "Gothic action RPG",
                LocalDate.of(2015, 3, 24),
                "https://example.com/bb.jpg",
                Genre.RPG,
                Collections.emptySet()
        );

        when(gameService.save(any(GameRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.title").value("Bloodborne"));
    }

    @Test
    @DisplayName("DELETE /api/games/{id} - Should return 204 No Content when game exists")
    void deleteById_ShouldReturnNoContent_WhenGameExists() throws Exception {
        when(gameService.deleteById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/games/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/games/{id} - Should return 409 Conflict when game is in user libraries")
    void deleteById_ShouldReturnConflict_WhenGameIsInUse() throws Exception {
        when(gameService.deleteById(1L))
                .thenThrow(new ResourceInUseException("Game is in one or more users' libraries and cannot be deleted"));

        mockMvc.perform(delete("/api/games/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Game is in one or more users' libraries and cannot be deleted"));
    }
}