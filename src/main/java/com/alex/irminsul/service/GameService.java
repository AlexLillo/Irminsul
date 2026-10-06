package com.alex.irminsul.service;

import com.alex.irminsul.dto.GameRequestDTO;
import com.alex.irminsul.dto.GameResponseDTO;
import com.alex.irminsul.dto.PlatformResponseDTO;
import com.alex.irminsul.entity.Game;
import com.alex.irminsul.entity.Platform;
import com.alex.irminsul.exception.DuplicateResourceException;
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

    private GameResponseDTO toResponseDTO(Game game) {
        Set<PlatformResponseDTO> platforms = game.getPlatforms()
                .stream()
                .map(platform -> new PlatformResponseDTO(
                        platform.getId(),
                        platform.getName()
                ))
                .collect(Collectors.toSet());

        return new GameResponseDTO(
                game.getId(),
                game.getTitle(),
                game.getDescription(),
                game.getReleaseDate(),
                game.getCoverUrl(),
                game.getGenre(),
                platforms
        );
    }

    public List<GameResponseDTO> findAll() {
        return gameRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Optional<GameResponseDTO> findById(Long id) {
        return gameRepository.findById(id)
                .map(this::toResponseDTO);
    }

    private Set<Platform> findExistingPlatforms(Set<Long> platformIds) {
        return platformIds.stream()
                .map(platformId -> platformRepository.findById(platformId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Platform not found: " + platformId)))
                .collect(Collectors.toSet());
    }

    public GameResponseDTO save(GameRequestDTO gameRequestDTO) {
        if (gameRepository.existsByTitle(gameRequestDTO.getTitle())) {
            throw new DuplicateResourceException(
                    "Game already exists: " + gameRequestDTO.getTitle());
        }

        Game game = new Game();
        game.setTitle(gameRequestDTO.getTitle());
        game.setDescription(gameRequestDTO.getDescription());
        game.setReleaseDate(gameRequestDTO.getReleaseDate());
        game.setCoverUrl(gameRequestDTO.getCoverUrl());
        game.setGenre(gameRequestDTO.getGenre());

        if (gameRequestDTO.getPlatformIds() != null) {
            game.setPlatforms(
                    findExistingPlatforms(gameRequestDTO.getPlatformIds())
            );
        }

        Game savedGame = gameRepository.save(game);

        return toResponseDTO(savedGame);
    }

    public Optional<GameResponseDTO> update(Long id, GameRequestDTO gameRequestDTO) {
        if (gameRepository.existsByTitleAndIdNot(
                gameRequestDTO.getTitle(), id)) {

            throw new DuplicateResourceException(
                    "Game already exists: " + gameRequestDTO.getTitle());
        }

        return gameRepository.findById(id)
                .map(existingGame -> {
                    existingGame.setTitle(gameRequestDTO.getTitle());
                    existingGame.setDescription(gameRequestDTO.getDescription());
                    existingGame.setReleaseDate(gameRequestDTO.getReleaseDate());
                    existingGame.setCoverUrl(gameRequestDTO.getCoverUrl());
                    existingGame.setGenre(gameRequestDTO.getGenre());

                    if (gameRequestDTO.getPlatformIds() != null) {
                        existingGame.setPlatforms(
                                findExistingPlatforms(
                                        gameRequestDTO.getPlatformIds())
                        );
                    }

                    Game updatedGame = gameRepository.save(existingGame);

                    return toResponseDTO(updatedGame);
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
