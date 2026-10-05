package com.alex.irminsul.service;

import com.alex.irminsul.entity.Game;
import com.alex.irminsul.entity.User;
import com.alex.irminsul.entity.UserGame;
import com.alex.irminsul.entity.UserGameId;
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

    public List<UserGame> findAll() {
        return userGameRepository.findAll();
    }

    public List<UserGame> findByUserId(Long userId) {
        return userGameRepository.findByUserId(userId);
    }

    public Optional<UserGame> findByUsernameAndGameId(String username, Long gameId) {
        return userGameRepository.findByUserUsernameAndGameId(username, gameId);
    }

    public UserGame saveByUsername(String username, UserGame userGame) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + username));

        Game game = gameRepository.findById(userGame.getGame().getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Game not found: " + userGame.getGame().getId()));

        if (userGameRepository
                .findByUserUsernameAndGameId(username, game.getId())
                .isPresent()) {

            throw new DuplicateResourceException(
                    "Game already exists in user's collection");
        }

        userGame.setUser(user);
        userGame.setGame(game);

        return userGameRepository.save(userGame);
    }

    public Optional<UserGame> updateByUsername(
            String username,
            Long gameId,
            UserGame userGame) {

        return userGameRepository
                .findByUserUsernameAndGameId(username, gameId)
                .map(existingUserGame -> {
                    existingUserGame.setStatus(userGame.getStatus());
                    existingUserGame.setRating(userGame.getRating());
                    existingUserGame.setNotes(userGame.getNotes());

                    return userGameRepository.save(existingUserGame);
                });
    }

    public boolean deleteByUsernameAndGameId(String username, Long gameId) {
        return userGameRepository.findByUserUsernameAndGameId(username, gameId)
                .map(userGame -> {
                    userGameRepository.delete(userGame);
                    return true;
                })
                .orElse(false);
    }

}