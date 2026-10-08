package com.alex.irminsul.service;

import com.alex.irminsul.dto.UserGameRequestDTO;
import com.alex.irminsul.dto.UserGameResponseDTO;
import com.alex.irminsul.entity.Game;
import com.alex.irminsul.entity.User;
import com.alex.irminsul.entity.UserGame;
import com.alex.irminsul.exception.DuplicateResourceException;
import com.alex.irminsul.exception.ResourceNotFoundException;
import com.alex.irminsul.repository.GameRepository;
import com.alex.irminsul.repository.UserGameRepository;
import com.alex.irminsul.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserGameService {

    private final UserGameRepository userGameRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public UserGameService(UserGameRepository userGameRepository,
                           UserRepository userRepository,
                           GameRepository gameRepository) {
        this.userGameRepository = userGameRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    private UserGameResponseDTO toResponseDTO(UserGame userGame) {
        Game game = userGame.getGame();

        return new UserGameResponseDTO(
                game.getId(),
                game.getTitle(),
                game.getCoverUrl(),
                userGame.getStatus(),
                userGame.getRating(),
                userGame.getNotes()
        );
    }

    public List<UserGameResponseDTO> findAll() {
        return userGameRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public UserGameResponseDTO saveByUsername(
            String username,
            UserGameRequestDTO userGameRequestDTO) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + username));

        Game game = gameRepository.findById(userGameRequestDTO.getGameId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Game not found: "
                                        + userGameRequestDTO.getGameId()));

        if (userGameRepository
                .findByUserUsernameAndGameId(username, game.getId())
                .isPresent()) {

            throw new DuplicateResourceException(
                    "Game already exists in user's collection");
        }

        UserGame userGame = new UserGame();
        userGame.setUser(user);
        userGame.setGame(game);
        userGame.setStatus(userGameRequestDTO.getStatus());
        userGame.setRating(userGameRequestDTO.getRating());
        userGame.setNotes(userGameRequestDTO.getNotes());

        UserGame savedUserGame = userGameRepository.save(userGame);

        return toResponseDTO(savedUserGame);
    }

    public List<UserGameResponseDTO> findByUserId(Long userId) {
        return userGameRepository.findByUserId(userId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<UserGameResponseDTO> findByUsername(String username) {
        return userGameRepository.findByUserUsername(username)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Optional<UserGameResponseDTO> findByUsernameAndGameId(
            String username,
            Long gameId) {

        return userGameRepository
                .findByUserUsernameAndGameId(username, gameId)
                .map(this::toResponseDTO);
    }

    public Optional<UserGameResponseDTO> updateByUsername(
            String username,
            Long gameId,
            UserGameRequestDTO userGameRequestDTO) {

        return userGameRepository
                .findByUserUsernameAndGameId(username, gameId)
                .map(existingUserGame -> {
                    existingUserGame.setStatus(
                            userGameRequestDTO.getStatus());

                    existingUserGame.setRating(
                            userGameRequestDTO.getRating());

                    existingUserGame.setNotes(
                            userGameRequestDTO.getNotes());

                    UserGame updatedUserGame =
                            userGameRepository.save(existingUserGame);

                    return toResponseDTO(updatedUserGame);
                });
    }

    public boolean deleteByUsernameAndGameId(
            String username,
            Long gameId) {

        return userGameRepository
                .findByUserUsernameAndGameId(username, gameId)
                .map(userGame -> {
                    userGameRepository.delete(userGame);
                    return true;
                })
                .orElse(false);
    }
}