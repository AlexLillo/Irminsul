package com.alex.irminsul.controller;

import com.alex.irminsul.dto.GameRequestDTO;
import com.alex.irminsul.dto.GameResponseDTO;
import com.alex.irminsul.entity.Game;
import com.alex.irminsul.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public List<GameResponseDTO> findAll() {
        return gameService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameResponseDTO> findById(@PathVariable Long id) {
        return gameService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public GameResponseDTO save(
            @RequestBody @Valid GameRequestDTO gameRequestDTO) {

        return gameService.save(gameRequestDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid GameRequestDTO gameRequestDTO) {

        return gameService.update(id, gameRequestDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (gameService.deleteById(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

}
