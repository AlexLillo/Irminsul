package com.alex.irminsul.service;

import com.alex.irminsul.dto.GameRequestDTO;
import com.alex.irminsul.dto.GameResponseDTO;
import com.alex.irminsul.dto.PlatformResponseDTO;
import com.alex.irminsul.entity.Game;
import com.alex.irminsul.entity.Platform;
import com.alex.irminsul.exception.DuplicateResourceException;
import com.alex.irminsul.exception.ResourceNotFoundException;
import com.alex.irminsul.repository.GameRepository;
import com.alex.irminsul.repository.PlatformRepository;
import com.alex.irminsul.repository.UserGameRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final PlatformRepository platformRepository;
    private final UserGameRepository userGameRepository;

    public GameService(GameRepository gameRepository,
                       PlatformRepository platformRepository,
                       UserGameRepository userGameRepository) {
        this.gameRepository = gameRepository;
        this.platformRepository = platformRepository;
        this.userGameRepository = userGameRepository;
    }

    private GameResponseDTO toResponseDTO(
            Game game,
            Double averageRating,
            Long ratingCount) {

        Set<PlatformResponseDTO> platforms = game.getPlatforms()
                .stream()
                .map(platform -> new PlatformResponseDTO(
                        platform.getId(),
                        platform.getName()
                ))
                .collect(Collectors.toSet());

        GameResponseDTO dto = new GameResponseDTO(
                game.getId(),
                game.getTitle(),
                game.getDescription(),
                game.getReleaseDate(),
                game.getCoverUrl(),
                game.getGenre(),
                platforms
        );

        dto.setAverageRating(averageRating);
        dto.setRatingCount(ratingCount);

        return dto;
    }

    private GameResponseDTO toResponseDTO(Game game) {
        List<Object[]> statisticsRows =
                userGameRepository.findRatingStatisticsByGameId(game.getId());

        Object[] statistics = statisticsRows.isEmpty()
                ? new Object[]{null, 0L}
                : statisticsRows.get(0);

        Double averageRating = statistics[0] == null
                ? null
                : ((Number) statistics[0]).doubleValue();

        Long ratingCount = ((Number) statistics[1]).longValue();

        return toResponseDTO(game, averageRating, ratingCount);
    }

    public List<GameResponseDTO> findAll() {
        Map<Long, Object[]> statisticsByGameId = new HashMap<>();

        for (Object[] statistics :
                userGameRepository.findRatingStatistics()) {

            Long gameId = ((Number) statistics[0]).longValue();
            statisticsByGameId.put(gameId, statistics);
        }

        return gameRepository.findAll()
                .stream()
                .map(game -> {
                    Object[] statistics =
                            statisticsByGameId.get(game.getId());

                    Double averageRating = statistics == null
                            ? null
                            : ((Number) statistics[1]).doubleValue();

                    Long ratingCount = statistics == null
                            ? 0L
                            : ((Number) statistics[2]).longValue();

                    return toResponseDTO(
                            game,
                            averageRating,
                            ratingCount
                    );
                })
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

    public Optional<GameResponseDTO> update(
            Long id,
            GameRequestDTO gameRequestDTO) {

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

        if (userGameRepository.existsByGameId(id)) {
            throw new DuplicateResourceException(
                    "Game is in one or more users' libraries and cannot be deleted");
        }

        gameRepository.deleteById(id);
        return true;
    }
}

