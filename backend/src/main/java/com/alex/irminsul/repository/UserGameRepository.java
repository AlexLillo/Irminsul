package com.alex.irminsul.repository;

import com.alex.irminsul.entity.UserGame;
import com.alex.irminsul.entity.UserGameId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserGameRepository extends JpaRepository<UserGame, UserGameId> {

    List<UserGame> findByUserId(Long userId);

    List<UserGame> findByUserUsername(String username);

    Optional<UserGame> findByUserUsernameAndGameId(String username, Long gameId);

    @Query("""
            SELECT ug.game.id, AVG(ug.rating), COUNT(ug.rating)
            FROM UserGame ug
            WHERE ug.rating IS NOT NULL
            GROUP BY ug.game.id
            """)
    List<Object[]> findRatingStatistics();

    @Query("""
        SELECT AVG(ug.rating), COUNT(ug.rating)
        FROM UserGame ug
        WHERE ug.game.id = :gameId
          AND ug.rating IS NOT NULL
        """)
    List<Object[]> findRatingStatisticsByGameId(@Param("gameId") Long gameId);
}
