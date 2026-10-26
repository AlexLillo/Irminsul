package com.alex.irminsul.service;

import com.alex.irminsul.dto.UserGameRequestDTO;
import com.alex.irminsul.dto.UserGameResponseDTO;
import com.alex.irminsul.entity.Game;
import com.alex.irminsul.entity.Status;
import com.alex.irminsul.entity.User;
import com.alex.irminsul.entity.UserGame;
import com.alex.irminsul.exception.DuplicateResourceException;
import com.alex.irminsul.exception.ResourceNotFoundException;
import com.alex.irminsul.repository.GameRepository;
import com.alex.irminsul.repository.UserGameRepository;
import com.alex.irminsul.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserGameServiceTest {

    @Mock
    private UserGameRepository userGameRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private UserGameService userGameService;

    @Test
    @DisplayName("Should successfully add a game to user's library when data is valid")
    void saveByUsername_ShouldSaveUserGame_WhenValid() {
        String username = "alex";
        Long gameId = 1L;
        UserGameRequestDTO request = new UserGameRequestDTO(gameId, Status.PLAYING, 9, "Masterpiece");

        User user = new User();
        user.setId(10L);
        user.setUsername(username);

        Game game = new Game();
        game.setId(gameId);
        game.setTitle("Bloodborne");
        game.setCoverUrl("https://example.com/bloodborne.jpg");

        UserGame savedUserGame = new UserGame();
        savedUserGame.setUser(user);
        savedUserGame.setGame(game);
        savedUserGame.setStatus(Status.PLAYING);
        savedUserGame.setRating(9);
        savedUserGame.setNotes("Masterpiece");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(userGameRepository.findByUserUsernameAndGameId(username, gameId)).thenReturn(Optional.empty());
        when(userGameRepository.save(any(UserGame.class))).thenReturn(savedUserGame);

        UserGameResponseDTO response = userGameService.saveByUsername(username, request);

        assertThat(response).isNotNull();
        assertThat(response.getGameId()).isEqualTo(gameId);
        assertThat(response.getGameTitle()).isEqualTo("Bloodborne");
        assertThat(response.getStatus()).isEqualTo(Status.PLAYING);
        assertThat(response.getRating()).isEqualTo(9);
        assertThat(response.getNotes()).isEqualTo("Masterpiece");

        verify(userGameRepository).save(any(UserGame.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user does not exist")
    void saveByUsername_ShouldThrowException_WhenUserNotFound() {
        String username = "ghost";
        UserGameRequestDTO request = new UserGameRequestDTO(1L, Status.BACKLOG, null, null);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userGameService.saveByUsername(username, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found: ghost");

        verify(userGameRepository, never()).save(any(UserGame.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when game does not exist")
    void saveByUsername_ShouldThrowException_WhenGameNotFound() {
        String username = "alex";
        Long invalidGameId = 999L;
        UserGameRequestDTO request = new UserGameRequestDTO(invalidGameId, Status.BACKLOG, null, null);

        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(gameRepository.findById(invalidGameId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userGameService.saveByUsername(username, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Game not found: 999");

        verify(userGameRepository, never()).save(any(UserGame.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when game already exists in user's collection")
    void saveByUsername_ShouldThrowException_WhenGameAlreadyInCollection() {
        String username = "alex";
        Long gameId = 1L;
        UserGameRequestDTO request = new UserGameRequestDTO(gameId, Status.COMPLETED, 10, "Finished it");

        User user = new User();
        user.setUsername(username);

        Game game = new Game();
        game.setId(gameId);

        UserGame existingEntry = new UserGame();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(userGameRepository.findByUserUsernameAndGameId(username, gameId)).thenReturn(Optional.of(existingEntry));

        assertThatThrownBy(() -> userGameService.saveByUsername(username, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Game already exists in user's collection");

        verify(userGameRepository, never()).save(any(UserGame.class));
    }

    @Test
    @DisplayName("Should return user game entry when it exists for the given username and game id")
    void findByUsernameAndGameId_ShouldReturnEntry_WhenExists() {
        String username = "alex";
        Long gameId = 1L;

        Game game = new Game();
        game.setId(gameId);
        game.setTitle("Elden Ring");

        UserGame userGame = new UserGame();
        userGame.setGame(game);
        userGame.setStatus(Status.PLAYING);

        when(userGameRepository.findByUserUsernameAndGameId(username, gameId)).thenReturn(Optional.of(userGame));

        Optional<UserGameResponseDTO> result = userGameService.findByUsernameAndGameId(username, gameId);

        assertThat(result).isPresent();
        assertThat(result.get().getGameTitle()).isEqualTo("Elden Ring");
        assertThat(result.get().getStatus()).isEqualTo(Status.PLAYING);
    }

    @Test
    @DisplayName("Should return empty optional when user game entry does not exist")
    void findByUsernameAndGameId_ShouldReturnEmpty_WhenDoesNotExist() {
        when(userGameRepository.findByUserUsernameAndGameId("alex", 99L)).thenReturn(Optional.empty());

        Optional<UserGameResponseDTO> result = userGameService.findByUsernameAndGameId("alex", 99L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should successfully update user game entry when it exists")
    void updateByUsername_ShouldUpdateEntry_WhenExists() {
        String username = "alex";
        Long gameId = 1L;
        UserGameRequestDTO updateRequest = new UserGameRequestDTO(gameId, Status.COMPLETED, 10, "Finished with platinum");

        Game game = new Game();
        game.setId(gameId);
        game.setTitle("Sekiro");

        UserGame existingUserGame = new UserGame();
        existingUserGame.setGame(game);
        existingUserGame.setStatus(Status.PLAYING);
        existingUserGame.setRating(8);
        existingUserGame.setNotes("Tough game");

        when(userGameRepository.findByUserUsernameAndGameId(username, gameId)).thenReturn(Optional.of(existingUserGame));
        when(userGameRepository.save(any(UserGame.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<UserGameResponseDTO> result = userGameService.updateByUsername(username, gameId, updateRequest);

        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo(Status.COMPLETED);
        assertThat(result.get().getRating()).isEqualTo(10);
        assertThat(result.get().getNotes()).isEqualTo("Finished with platinum");
        verify(userGameRepository).save(existingUserGame);
    }

    @Test
    @DisplayName("Should return empty optional when attempting to update a non-existing entry")
    void updateByUsername_ShouldReturnEmpty_WhenDoesNotExist() {
        String username = "alex";
        Long gameId = 50L;
        UserGameRequestDTO updateRequest = new UserGameRequestDTO(gameId, Status.COMPLETED, 10, null);

        when(userGameRepository.findByUserUsernameAndGameId(username, gameId)).thenReturn(Optional.empty());

        Optional<UserGameResponseDTO> result = userGameService.updateByUsername(username, gameId, updateRequest);

        assertThat(result).isEmpty();
        verify(userGameRepository, never()).save(any(UserGame.class));
    }

    @Test
    @DisplayName("Should delete entry and return true when it exists")
    void deleteByUsernameAndGameId_ShouldDeleteAndReturnTrue_WhenExists() {
        String username = "alex";
        Long gameId = 1L;
        UserGame userGame = new UserGame();

        when(userGameRepository.findByUserUsernameAndGameId(username, gameId)).thenReturn(Optional.of(userGame));

        boolean deleted = userGameService.deleteByUsernameAndGameId(username, gameId);

        assertThat(deleted).isTrue();
        verify(userGameRepository).delete(userGame);
    }

    @Test
    @DisplayName("Should return false when attempting to delete a non-existing entry")
    void deleteByUsernameAndGameId_ShouldReturnFalse_WhenDoesNotExist() {
        when(userGameRepository.findByUserUsernameAndGameId("alex", 99L)).thenReturn(Optional.empty());

        boolean deleted = userGameService.deleteByUsernameAndGameId("alex", 99L);

        assertThat(deleted).isFalse();
        verify(userGameRepository, never()).delete(any(UserGame.class));
    }
}