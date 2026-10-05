package com.alex.irminsul.service;

import com.alex.irminsul.entity.Game;
import com.alex.irminsul.entity.User;
import com.alex.irminsul.entity.UserGame;
import com.alex.irminsul.entity.UserGameId;
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

    public Optional<UserGame> findById(UserGameId id) {
        return userGameRepository.findById(id);
    }

    public List<UserGame> findByUserId(Long userId) {
        return userGameRepository.findByUserId(userId);
    }

    public Optional<UserGame> findByUserIdAndGameId(Long userId, Long gameId) {
        return userGameRepository.findByUserIdAndGameId(userId, gameId);
    }

    public UserGame save(UserGame userGame) {
        User user = userRepository.findById(userGame.getUser().getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + userGame.getUser().getId()));

        Game game = gameRepository.findById(userGame.getGame().getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Game not found: " + userGame.getGame().getId()));

        userGame.setUser(user);
        userGame.setGame(game);

        return userGameRepository.save(userGame);
    }

    public Optional<UserGame> update(Long userId, Long gameId, UserGame userGame) {
        return userGameRepository.findByUserIdAndGameId(userId, gameId)
                .map(existingUserGame -> {
                    existingUserGame.setStatus(userGame.getStatus());
                    existingUserGame.setRating(userGame.getRating());
                    existingUserGame.setNotes(userGame.getNotes());

                    return userGameRepository.save(existingUserGame);
                });
    }

    public boolean deleteByUserIdAndGameId(Long userId, Long gameId) {
        return userGameRepository.findByUserIdAndGameId(userId, gameId)
                .map(userGame -> {
                    userGameRepository.delete(userGame);
                    return true;
                })
                .orElse(false);
    }
}