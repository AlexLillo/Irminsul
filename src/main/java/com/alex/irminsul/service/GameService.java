package com.alex.irminsul.service;

import com.alex.irminsul.entity.Game;
import com.alex.irminsul.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<Game> findById(Long id) {
        return gameRepository.findById(id);
    }

    public Optional<Game> update(Long id, Game game) {
        return gameRepository.findById(id)
                .map(existingGame -> {
                    existingGame.setTitle(game.getTitle());
                    existingGame.setDescription(game.getDescription());
                    existingGame.setReleaseDate(game.getReleaseDate());
                    existingGame.setCoverUrl(game.getCoverUrl());
                    existingGame.setGenre(game.getGenre());

                    return gameRepository.save(existingGame);
                });
    }

}
