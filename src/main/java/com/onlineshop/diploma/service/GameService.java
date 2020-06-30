package com.onlineshop.diploma.service;

import com.onlineshop.diploma.domain.Game;

import java.util.List;

public interface GameService {
    List<Game> findAll();

    Game findOne(Long id);
}
