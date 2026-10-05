package com.alex.irminsul.repository;

import com.alex.irminsul.entity.UserGame;
import com.alex.irminsul.entity.UserGameId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserGameRepository extends JpaRepository<UserGame, UserGameId> {

    List<UserGame> findByUserId(Long userId);

    Optional<UserGame> findByUserIdAndGameId(Long userId, Long gameId);
}