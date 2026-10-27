package com.alex.irminsul.repository;

import com.alex.irminsul.entity.Game;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    @Override
    @EntityGraph(attributePaths = {"platforms"})
    List<Game> findAll();

    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, Long id);

    boolean existsByPlatformsId(Long platformId);
}