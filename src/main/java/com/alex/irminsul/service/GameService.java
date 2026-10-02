package com.alex.irminsul.service;

import com.alex.irminsul.entity.Game;
import com.alex.irminsul.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {
    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<Game> findAll() {
        return gameRepository.findAll();
    }

    public Game save(Game game) {
        return gameRepository.save(game);
    }
}
