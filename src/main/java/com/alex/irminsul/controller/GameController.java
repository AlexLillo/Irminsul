package com.alex.irminsul.controller;

import com.alex.irminsul.entity.Game;
import com.alex.irminsul.service.GameService;
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
    public List<Game> findAll() {
        return gameService.findAll();
    }

    @PostMapping
    public Game save(@RequestBody Game game) {
        return gameService.save(game);
    }
}
