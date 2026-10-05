package com.alex.irminsul.controller;

import com.alex.irminsul.entity.UserGame;
import com.alex.irminsul.service.UserGameService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{userId}/{gameId}")
    public ResponseEntity<UserGame> findByUserIdAndGameId(
            @PathVariable Long userId,
            @PathVariable Long gameId) {

        return userGameService.findByUserIdAndGameId(userId, gameId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public UserGame save(@RequestBody @Valid UserGame userGame) {
        return userGameService.save(userGame);
    }

    @PutMapping("/{userId}/{gameId}")
    public ResponseEntity<UserGame> update(
            @PathVariable Long userId,
            @PathVariable Long gameId,
            @RequestBody @Valid UserGame userGame) {

        return userGameService.update(userId, gameId, userGame)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{userId}/{gameId}")
    public ResponseEntity<Void> deleteByUserIdAndGameId(
            @PathVariable Long userId,
            @PathVariable Long gameId) {

        if (userGameService.deleteByUserIdAndGameId(userId, gameId)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}