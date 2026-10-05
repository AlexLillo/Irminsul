package com.alex.irminsul.controller;

import com.alex.irminsul.entity.UserGame;
import com.alex.irminsul.service.UserGameService;
import jakarta.validation.Valid;
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
    public List<UserGame> findAll() {
        return userGameService.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<UserGame> findByUserId(@PathVariable Long userId) {
        return userGameService.findByUserId(userId);
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<UserGame> findByGameId(
            @PathVariable Long gameId,
            Authentication authentication) {

        String username = authentication.getName();

        return userGameService.findByUsernameAndGameId(username, gameId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public UserGame save(
            @RequestBody @Valid UserGame userGame,
            Authentication authentication) {

        String username = authentication.getName();

        return userGameService.saveByUsername(username, userGame);
    }

    @PutMapping("/{gameId}")
    public ResponseEntity<UserGame> update(
            @PathVariable Long gameId,
            @RequestBody @Valid UserGame userGame,
            Authentication authentication) {

        String username = authentication.getName();

        return userGameService.updateByUsername(
                        username,
                        gameId,
                        userGame)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{gameId}")
    public ResponseEntity<Void> deleteByGameId(
            @PathVariable Long gameId,
            Authentication authentication) {

        String username = authentication.getName();

        if (userGameService.deleteByUsernameAndGameId(username, gameId)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}