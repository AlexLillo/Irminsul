package com.alex.irminsul.controller;

import com.alex.irminsul.dto.PlatformRequestDTO;
import com.alex.irminsul.dto.PlatformResponseDTO;
import com.alex.irminsul.exception.ResourceInUseException;
import com.alex.irminsul.service.PlatformService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlatformController.class)
@AutoConfigureMockMvc(addFilters = false)
class PlatformControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlatformService platformService;

    @Test
    @DisplayName("GET /api/platforms - Should return 200 OK and platform list")
    void findAll_ShouldReturnPlatformList_WhenCalled() throws Exception {
        PlatformResponseDTO platform = new PlatformResponseDTO(1L, "PlayStation 5");

        when(platformService.findAll()).thenReturn(List.of(platform));

        mockMvc.perform(get("/api/platforms"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("PlayStation 5"));
    }

    @Test
    @DisplayName("GET /api/platforms/{id} - Should return 200 OK when platform exists")
    void findById_ShouldReturnPlatform_WhenExists() throws Exception {
        PlatformResponseDTO platform = new PlatformResponseDTO(1L, "Nintendo Switch");

        when(platformService.findById(1L)).thenReturn(Optional.of(platform));

        mockMvc.perform(get("/api/platforms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Nintendo Switch"));
    }

    @Test
    @DisplayName("GET /api/platforms/{id} - Should return 404 Not Found when platform does not exist")
    void findById_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        when(platformService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/platforms/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/platforms - Should return 400 Bad Request when platform name is blank")
    void save_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {
        String invalidPayload = """
                {
                    "name": ""
                }
                """;

        mockMvc.perform(post("/api/platforms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    @DisplayName("POST /api/platforms - Should return 200 OK and created platform when valid")
    void save_ShouldReturnSavedPlatform_WhenValid() throws Exception {
        String validPayload = """
                {
                    "name": "PC"
                }
                """;

        PlatformResponseDTO response = new PlatformResponseDTO(3L, "PC");
        when(platformService.save(any(PlatformRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/platforms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("PC"));
    }

    @Test
    @DisplayName("DELETE /api/platforms/{id} - Should return 204 No Content when platform is deleted")
    void deleteById_ShouldReturnNoContent_WhenDeleted() throws Exception {
        when(platformService.deleteById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/platforms/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/platforms/{id} - Should return 409 Conflict when platform is in use by games")
    void deleteById_ShouldReturnConflict_WhenPlatformIsInUse() throws Exception {
        when(platformService.deleteById(1L))
                .thenThrow(new ResourceInUseException("Platform is used by one or more games"));

        mockMvc.perform(delete("/api/platforms/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Platform is used by one or more games"));
    }
}