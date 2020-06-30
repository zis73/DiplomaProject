package com.onlineshop.diploma.service.impl;

import com.onlineshop.diploma.domain.Game;
import com.onlineshop.diploma.repository.GameRepository;
import com.onlineshop.diploma.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameServiceImpl implements GameService {
    @Autowired
    private GameRepository gameRepository;


    public List<Game> findAll() {
        return (List<Game>) gameRepository.findAll();
    }

    public Game findOne(Long id) {
        return gameRepository.findOne(id);
    }

}
