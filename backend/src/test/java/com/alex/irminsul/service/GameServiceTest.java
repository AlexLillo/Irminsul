package com.alex.irminsul.service;

import com.alex.irminsul.dto.GameRequestDTO;
import com.alex.irminsul.dto.GameResponseDTO;
import com.alex.irminsul.dto.PlatformResponseDTO;
import com.alex.irminsul.entity.Game;
import com.alex.irminsul.entity.Genre;
import com.alex.irminsul.entity.Platform;
import com.alex.irminsul.exception.DuplicateResourceException;
import com.alex.irminsul.exception.ResourceInUseException;
import com.alex.irminsul.exception.ResourceNotFoundException;
import com.alex.irminsul.repository.GameRepository;
import com.alex.irminsul.repository.PlatformRepository;
import com.alex.irminsul.repository.UserGameRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PlatformRepository platformRepository;

    @Mock
    private UserGameRepository userGameRepository;

    @InjectMocks
    private GameService gameService;

    @Test
    @DisplayName("Should return all games mapped with their rating statistics")
    void findAll_ShouldReturnAllGamesWithStatistics() {
        Game game = new Game();
        game.setId(1L);
        game.setTitle("Elden Ring");
        game.setGenre(Genre.RPG);

        List<Object[]> stats = Collections.singletonList(new Object[]{1L, 9.5, 10L});

        when(userGameRepository.findRatingStatistics()).thenReturn(stats);
        when(gameRepository.findAll()).thenReturn(List.of(game));

        List<GameResponseDTO> result = gameService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle()).isEqualTo("Elden Ring");
        assertThat(result.getFirst().getAverageRating()).isEqualTo(9.5);
        assertThat(result.getFirst().getRatingCount()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Should return game by id when it exists")
    void findById_ShouldReturnGame_WhenGameExists() {
        Game game = new Game();
        game.setId(1L);
        game.setTitle("Hollow Knight");
        game.setGenre(Genre.PLATFORMER);

        List<Object[]> stats = Collections.singletonList(new Object[]{9.0, 4L});

        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(userGameRepository.findRatingStatisticsByGameId(1L)).thenReturn(stats);

        Optional<GameResponseDTO> result = gameService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Hollow Knight");
        assertThat(result.get().getAverageRating()).isEqualTo(9.0);
        assertThat(result.get().getRatingCount()).isEqualTo(4L);
    }

    @Test
    @DisplayName("Should return empty optional when game id does not exist")
    void findById_ShouldReturnEmpty_WhenGameDoesNotExist() {
        when(gameRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<GameResponseDTO> result = gameService.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should successfully save a game when data and platforms are valid")
    void save_ShouldSaveGame_WhenDataIsValid() {
        GameRequestDTO request = new GameRequestDTO(
                "Celeste",
                "A platformer about climbing a mountain",
                LocalDate.of(2018, 1, 25),
                "https://example.com/celeste.jpg",
                Genre.PLATFORMER,
                Set.of(1L)
        );

        Platform platform = new Platform();
        platform.setId(1L);
        platform.setName("PC");

        Game savedGame = new Game();
        savedGame.setId(10L);
        savedGame.setTitle(request.getTitle());
        savedGame.setDescription(request.getDescription());
        savedGame.setReleaseDate(request.getReleaseDate());
        savedGame.setCoverUrl(request.getCoverUrl());
        savedGame.setGenre(request.getGenre());
        savedGame.setPlatforms(Set.of(platform));

        when(gameRepository.existsByTitle("Celeste")).thenReturn(false);
        when(platformRepository.findById(1L)).thenReturn(Optional.of(platform));
        when(gameRepository.save(any(Game.class))).thenReturn(savedGame);

        GameResponseDTO response = gameService.save(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getTitle()).isEqualTo("Celeste");
        assertThat(response.getPlatforms()).hasSize(1);
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when saving game with existing title")
    void save_ShouldThrowException_WhenTitleAlreadyExists() {
        GameRequestDTO request = new GameRequestDTO(
                "Celeste",
                null,
                null,
                null,
                Genre.PLATFORMER,
                null
        );

        when(gameRepository.existsByTitle("Celeste")).thenReturn(true);

        assertThatThrownBy(() -> gameService.save(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Game already exists: Celeste");

        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when assigned platform does not exist")
    void save_ShouldThrowException_WhenPlatformDoesNotExist() {
        GameRequestDTO request = new GameRequestDTO(
                "Hades",
                null,
                null,
                null,
                Genre.ACTION,
                Set.of(99L)
        );

        when(gameRepository.existsByTitle("Hades")).thenReturn(false);
        when(platformRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameService.save(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Platform not found: 99");

        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    @DisplayName("Should update existing game when data is valid")
    void update_ShouldUpdateGame_WhenDataIsValid() {
        GameRequestDTO request = new GameRequestDTO(
                "Metroid Dread",
                "Action adventure",
                LocalDate.of(2021, 10, 8),
                null,
                Genre.ADVENTURE,
                null
        );

        Game existingGame = new Game();
        existingGame.setId(2L);
        existingGame.setTitle("Metroid 5");

        when(gameRepository.existsByTitleAndIdNot("Metroid Dread", 2L)).thenReturn(false);
        when(gameRepository.findById(2L)).thenReturn(Optional.of(existingGame));
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<GameResponseDTO> result = gameService.update(2L, request);

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Metroid Dread");
        assertThat(result.get().getDescription()).isEqualTo("Action adventure");
        verify(gameRepository).save(existingGame);
    }

    @Test
    @DisplayName("Should return false when attempting to delete a non-existing game")
    void deleteById_ShouldReturnFalse_WhenGameDoesNotExist() {
        when(gameRepository.existsById(50L)).thenReturn(false);

        boolean deleted = gameService.deleteById(50L);

        assertThat(deleted).isFalse();
        verify(gameRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should throw ResourceInUseException when deleting a game present in user libraries")
    void deleteById_ShouldThrowException_WhenGameIsInUserLibrary() {
        when(gameRepository.existsById(1L)).thenReturn(true);
        when(userGameRepository.existsByGameId(1L)).thenReturn(true);

        assertThatThrownBy(() -> gameService.deleteById(1L))
                .isInstanceOf(ResourceInUseException.class)
                .hasMessageContaining("Game is in one or more users' libraries and cannot be deleted");

        verify(gameRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should successfully delete game when it exists and is not in any user library")
    void deleteById_ShouldDeleteGame_WhenValid() {
        when(gameRepository.existsById(1L)).thenReturn(true);
        when(userGameRepository.existsByGameId(1L)).thenReturn(false);

        boolean deleted = gameService.deleteById(1L);

        assertThat(deleted).isTrue();
        verify(gameRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should sort game platforms alphabetically case-insensitive in response DTO")
    void findById_ShouldReturnPlatformsSortedCaseInsensitively() {
        Platform p1 = new Platform();
        p1.setId(1L);
        p1.setName("Switch");

        Platform p2 = new Platform();
        p2.setId(2L);
        p2.setName("android");

        Platform p3 = new Platform();
        p3.setId(3L);
        p3.setName("PC");

        Game game = new Game();
        game.setId(1L);
        game.setTitle("Dead Cells");
        game.setGenre(Genre.ACTION);
        game.setPlatforms(Set.of(p1, p2, p3));

        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(userGameRepository.findRatingStatisticsByGameId(1L)).thenReturn(Collections.emptyList());

        Optional<GameResponseDTO> result = gameService.findById(1L);

        assertThat(result).isPresent();

        List<String> platformNames = result.get().getPlatforms().stream()
                .map(PlatformResponseDTO::getName)
                .toList();

        assertThat(platformNames).containsExactly("android", "PC", "Switch");
    }
}