package com.alex.irminsul.repository;

import com.alex.irminsul.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long>{
}
