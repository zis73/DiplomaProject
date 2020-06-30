package com.onlineshop.diploma.repository;

import com.onlineshop.diploma.domain.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, Long> {
    Game findOne(Long id);
}
