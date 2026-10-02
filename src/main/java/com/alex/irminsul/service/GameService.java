package com.alex.irminsul.service;

import com.alex.irminsul.entity.Game;
import com.alex.irminsul.entity.Platform;
import com.alex.irminsul.repository.GameRepository;
import com.alex.irminsul.repository.PlatformRepository;
import com.alex.irminsul.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final PlatformRepository platformRepository;

    public GameService(GameRepository gameRepository,
                       PlatformRepository platformRepository) {
        this.gameRepository = gameRepository;
        this.platformRepository = platformRepository;
    }

    public List<Game> findAll() {
        return gameRepository.findAll();
    }

    public Optional<Game> findById(Long id) {
        return gameRepository.findById(id);
    }

    private Set<Platform> findExistingPlatforms(Set<Platform> platforms) {
        return platforms.stream()
                .map(platform -> platformRepository.findById(platform.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Platform not found: " + platform.getId())))
                .collect(Collectors.toSet());
    }

    public Game save(Game game) {
        if (game.getPlatforms() != null) {
            game.setPlatforms(findExistingPlatforms(game.getPlatforms()));
        }

        return gameRepository.save(game);
    }

    public Optional<Game> update(Long id, Game game) {
        return gameRepository.findById(id)
                .map(existingGame -> {
                    existingGame.setTitle(game.getTitle());
                    existingGame.setDescription(game.getDescription());
                    existingGame.setReleaseDate(game.getReleaseDate());
                    existingGame.setCoverUrl(game.getCoverUrl());
                    existingGame.setGenre(game.getGenre());

                    if (game.getPlatforms() != null) {
                        existingGame.setPlatforms(findExistingPlatforms(game.getPlatforms()));
                    }

                    return gameRepository.save(existingGame);
                });
    }

    public boolean deleteById(Long id) {
        if (!gameRepository.existsById(id)) {
            return false;
        }

        gameRepository.deleteById(id);
        return true;
    }

}
