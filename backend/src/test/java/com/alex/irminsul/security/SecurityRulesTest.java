package com.alex.irminsul.security;

import com.alex.irminsul.config.SecurityConfig;
import com.alex.irminsul.controller.GameController;
import com.alex.irminsul.controller.UserController;
import com.alex.irminsul.controller.UserGameController;
import com.alex.irminsul.dto.GameResponseDTO;
import com.alex.irminsul.dto.UserResponseDTO;
import com.alex.irminsul.entity.Genre;
import com.alex.irminsul.entity.Role;
import com.alex.irminsul.service.CustomUserDetailsService;
import com.alex.irminsul.service.GameService;
import com.alex.irminsul.service.UserGameService;
import com.alex.irminsul.service.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        GameController.class,
        UserController.class,
        UserGameController.class
})
@Import({SecurityConfig.class, AuthenticationConfiguration.class})
class SecurityRulesTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService gameService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserGameService userGameService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private RequestPostProcessor csrfToken() {
        return request -> {
            request.setCookies(new Cookie("XSRF-TOKEN", "valid-csrf-token"));
            request.addHeader("X-XSRF-TOKEN", "valid-csrf-token");
            return request;
        };
    }

    @Test
    @DisplayName("GET /api/games - Should be public for anonymous users")
    void getGames_ShouldBePublic_ForAnonymousUser() throws Exception {
        when(gameService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/games - Should return 401 Unauthorized for anonymous users")
    void postGames_ShouldBeUnauthorized_ForAnonymousUser() throws Exception {
        String payload = """
                {"title": "Zelda", "genre": "ACTION"}
                """;

        mockMvc.perform(post("/api/games")
                        .with(csrfToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/games - Should return 403 Forbidden for authenticated users with role USER")
    void postGames_ShouldBeForbidden_ForRoleUser() throws Exception {
        String payload = """
                {"title": "Zelda", "genre": "ACTION"}
                """;

        mockMvc.perform(post("/api/games")
                        .with(csrfToken())
                        .with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/games - Should return 201 Created for users with role ADMIN and valid CSRF")
    void postGames_ShouldSucceed_ForRoleAdmin_WithCsrf() throws Exception {
        String payload = """
                {"title": "Zelda", "genre": "ACTION"}
                """;

        GameResponseDTO response = new GameResponseDTO(
                1L, "Zelda", null, null, null, Genre.ACTION, Collections.emptySet()
        );
        when(gameService.save(any())).thenReturn(response);

        mockMvc.perform(post("/api/games")
                        .with(csrfToken())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/games - Should return 403 Forbidden even for ADMIN if CSRF token is missing")
    void postGames_ShouldBeForbidden_ForAdmin_WhenCsrfMissing() throws Exception {
        String payload = """
                {"title": "Zelda", "genre": "ACTION"}
                """;

        mockMvc.perform(post("/api/games")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/users - Should return 401 Unauthorized for anonymous users")
    void getUsers_ShouldBeUnauthorized_ForAnonymousUser() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/users - Should return 403 Forbidden for authenticated users with role USER")
    void getUsers_ShouldBeForbidden_ForRoleUser() throws Exception {
        mockMvc.perform(get("/api/users")
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/users - Should return 200 OK for users with role ADMIN")
    void getUsers_ShouldSucceed_ForRoleAdmin() throws Exception {
        when(userService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/users")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/users (Register) - Should return 201 Created when CSRF token is present")
    void postUsers_ShouldBePublic_WithCsrf() throws Exception {
        String payload = """
                {
                    "username": "newuser",
                    "email": "user@hyrule.com",
                    "password": "Password123"
                }
                """;

        UserResponseDTO response = new UserResponseDTO(1L, "newuser", Role.USER);
        when(userService.save(any())).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .with(csrfToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/users (Register) - Should return 403 Forbidden when CSRF token is missing")
    void postUsers_ShouldBeForbidden_WhenCsrfMissing() throws Exception {
        String payload = """
                {
                    "username": "newuser",
                    "email": "user@hyrule.com",
                    "password": "Password123"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/user-games/me - Should return 401 Unauthorized for anonymous users")
    void getUserGamesMe_ShouldBeUnauthorized_ForAnonymousUser() throws Exception {
        mockMvc.perform(get("/api/user-games/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/user-games/me - Should return 200 OK for authenticated user")
    void getUserGamesMe_ShouldSucceed_ForAuthenticatedUser() throws Exception {
        when(userGameService.findByUsername(anyString())).thenReturn(List.of());

        mockMvc.perform(get("/api/user-games/me")
                        .with(user("link").roles("USER")))
                .andExpect(status().isOk());
    }
}