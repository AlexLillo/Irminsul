package com.alex.irminsul.controller;

import com.alex.irminsul.dto.UserRequestDTO;
import com.alex.irminsul.dto.UserResponseDTO;
import com.alex.irminsul.entity.Role;
import com.alex.irminsul.exception.DuplicateResourceException;
import com.alex.irminsul.service.UserService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private static final Authentication AUTH = new UsernamePasswordAuthenticationToken("alex", null);

    @Test
    @DisplayName("GET /api/users - Should return 200 OK and users list")
    void findAll_ShouldReturnUsersList_WhenCalled() throws Exception {
        UserResponseDTO user = new UserResponseDTO(1L, "alex", Role.USER);

        when(userService.findAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("alex"))
                .andExpect(jsonPath("$[0].role").value("USER"));
    }

    @Test
    @DisplayName("GET /api/users/me - Should return 200 OK and current profile for authenticated user")
    void findMe_ShouldReturnCurrentUser_WhenAuthenticated() throws Exception {
        UserResponseDTO user = new UserResponseDTO(1L, "alex", Role.USER);

        when(userService.findByUsername("alex")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/me").principal(AUTH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("alex"));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Should return 200 OK when user exists")
    void findById_ShouldReturnUser_WhenExists() throws Exception {
        UserResponseDTO user = new UserResponseDTO(5L, "zelda", Role.USER);

        when(userService.findById(5L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.username").value("zelda"));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Should return 404 Not Found when user does not exist")
    void findById_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        when(userService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/users - Should return 200 OK and created user when request is valid")
    void save_ShouldReturnSavedUser_WhenDataIsValid() throws Exception {
        String validPayload = """
                {
                    "username": "newuser",
                    "email": "newuser@example.com",
                    "password": "Password123"
                }
                """;

        UserResponseDTO response = new UserResponseDTO(10L, "newuser", Role.USER);
        when(userService.save(any(UserRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("POST /api/users - Should return 400 Bad Request when email format is invalid")
    void save_ShouldReturnBadRequest_WhenEmailIsInvalid() throws Exception {
        String invalidPayload = """
                {
                    "username": "validUser",
                    "email": "not-an-email",
                    "password": "Password123"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    @DisplayName("POST /api/users - Should return 400 Bad Request when password is under 8 characters")
    void save_ShouldReturnBadRequest_WhenPasswordIsTooShort() throws Exception {
        String invalidPayload = """
                {
                    "username": "validUser",
                    "email": "valid@example.com",
                    "password": "short"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").exists());
    }

    @Test
    @DisplayName("POST /api/users - Should return 409 Conflict when username or email already exists")
    void save_ShouldReturnConflict_WhenUserAlreadyExists() throws Exception {
        String payload = """
                {
                    "username": "existingUser",
                    "email": "existing@example.com",
                    "password": "Password123"
                }
                """;

        when(userService.save(any(UserRequestDTO.class)))
                .thenThrow(new DuplicateResourceException("Username already exists: existingUser"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Username already exists: existingUser"));
    }
}