package com.alex.irminsul.controller;

import com.alex.irminsul.dto.UserGameRequestDTO;
import com.alex.irminsul.dto.UserGameResponseDTO;
import com.alex.irminsul.entity.Status;
import com.alex.irminsul.service.UserGameService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserGameController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserGameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserGameService userGameService;

    private static final Authentication AUTH = new UsernamePasswordAuthenticationToken("alex", null);

    @Test
    @DisplayName("GET /api/user-games/me - Should return games for authenticated user")
    void findMyGames_ShouldReturnUserGames_WhenAuthenticated() throws Exception {
        UserGameResponseDTO game = new UserGameResponseDTO(
                1L,
                "Bloodborne",
                "https://example.com/cover.jpg",
                Status.PLAYING,
                9,
                "Masterpiece"
        );

        when(userGameService.findByUsername("alex")).thenReturn(List.of(game));

        mockMvc.perform(get("/api/user-games/me").principal(AUTH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].gameId").value(1L))
                .andExpect(jsonPath("$[0].gameTitle").value("Bloodborne"))
                .andExpect(jsonPath("$[0].status").value("PLAYING"))
                .andExpect(jsonPath("$[0].rating").value(9));
    }

    @Test
    @DisplayName("GET /api/user-games/{gameId} - Should return entry when it exists for authenticated user")
    void findByGameId_ShouldReturnEntry_WhenExists() throws Exception {
        UserGameResponseDTO game = new UserGameResponseDTO(
                1L,
                "Sekiro",
                null,
                Status.COMPLETED,
                10,
                "Hard but fair"
        );

        when(userGameService.findByUsernameAndGameId("alex", 1L)).thenReturn(Optional.of(game));

        mockMvc.perform(get("/api/user-games/1").principal(AUTH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value(1L))
                .andExpect(jsonPath("$.gameTitle").value("Sekiro"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("GET /api/user-games/{gameId} - Should return 404 Not Found when entry does not exist")
    void findByGameId_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        when(userGameService.findByUsernameAndGameId("alex", 99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/user-games/99").principal(AUTH))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/user-games - Should return 400 Bad Request when status is null")
    void save_ShouldReturnBadRequest_WhenStatusIsNull() throws Exception {
        String invalidPayload = """
                {
                    "gameId": 1,
                    "status": null,
                    "rating": 5,
                    "notes": "Testing"
                }
                """;

        mockMvc.perform(post("/api/user-games")
                        .principal(AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    @DisplayName("POST /api/user-games - Should return 200 OK and saved entry when payload is valid")
    void save_ShouldReturnSavedEntry_WhenValid() throws Exception {
        String validPayload = """
                {
                    "gameId": 1,
                    "status": "BACKLOG",
                    "rating": null,
                    "notes": null
                }
                """;

        UserGameResponseDTO response = new UserGameResponseDTO(
                1L,
                "Elden Ring",
                null,
                Status.BACKLOG,
                null,
                null
        );

        when(userGameService.saveByUsername(eq("alex"), any(UserGameRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/user-games")
                        .principal(AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value(1L))
                .andExpect(jsonPath("$.status").value("BACKLOG"));
    }

    @Test
    @DisplayName("PUT /api/user-games/{gameId} - Should return 200 OK and updated entry when entry exists")
    void update_ShouldReturnUpdatedEntry_WhenExists() throws Exception {
        String updatePayload = """
                {
                    "gameId": 1,
                    "status": "COMPLETED",
                    "rating": 10,
                    "notes": "Got 100% achievements"
                }
                """;

        UserGameResponseDTO response = new UserGameResponseDTO(
                1L,
                "Elden Ring",
                null,
                Status.COMPLETED,
                10,
                "Got 100% achievements"
        );

        when(userGameService.updateByUsername(eq("alex"), eq(1L), any(UserGameRequestDTO.class)))
                .thenReturn(Optional.of(response));

        mockMvc.perform(put("/api/user-games/1")
                        .principal(AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.rating").value(10))
                .andExpect(jsonPath("$.notes").value("Got 100% achievements"));
    }

    @Test
    @DisplayName("DELETE /api/user-games/{gameId} - Should return 204 No Content when entry is deleted")
    void deleteByGameId_ShouldReturnNoContent_WhenDeleted() throws Exception {
        when(userGameService.deleteByUsernameAndGameId("alex", 1L)).thenReturn(true);

        mockMvc.perform(delete("/api/user-games/1").principal(AUTH))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/user-games/{gameId} - Should return 404 Not Found when entry to delete does not exist")
    void deleteByGameId_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        when(userGameService.deleteByUsernameAndGameId("alex", 99L)).thenReturn(false);

        mockMvc.perform(delete("/api/user-games/99").principal(AUTH))
                .andExpect(status().isNotFound());
    }
}