package com.alex.irminsul.controller;

import com.alex.irminsul.dto.UserGameRequestDTO;
import com.alex.irminsul.dto.UserGameResponseDTO;
import com.alex.irminsul.service.UserGameService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-games")
public class UserGameController {

    private final UserGameService userGameService;

    public UserGameController(UserGameService userGameService) {
        this.userGameService = userGameService;
    }

    @GetMapping
    public List<UserGameResponseDTO> findAll() {
        return userGameService.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<UserGameResponseDTO> findByUserId(
            @PathVariable Long userId) {

        return userGameService.findByUserId(userId);
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<UserGameResponseDTO> findByGameId(
            @PathVariable Long gameId,
            Authentication authentication) {

        String username = authentication.getName();

        return userGameService
                .findByUsernameAndGameId(username, gameId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public List<UserGameResponseDTO> findMyGames(Authentication authentication) {
        return userGameService.findByUsername(authentication.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserGameResponseDTO save(
            @RequestBody @Valid UserGameRequestDTO userGameRequestDTO,
            Authentication authentication) {
        String username = authentication.getName();
        return userGameService.saveByUsername(username, userGameRequestDTO);
    }

    @PutMapping("/{gameId}")
    public ResponseEntity<UserGameResponseDTO> update(
            @PathVariable Long gameId,
            @RequestBody @Valid UserGameRequestDTO userGameRequestDTO,
            Authentication authentication) {

        String username = authentication.getName();

        return userGameService
                .updateByUsername(
                        username,
                        gameId,
                        userGameRequestDTO
                )
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{gameId}")
    public ResponseEntity<Void> deleteByGameId(
            @PathVariable Long gameId,
            Authentication authentication) {

        if (userGameService.deleteByUsernameAndGameId(
                authentication.getName(),
                gameId)) {

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}