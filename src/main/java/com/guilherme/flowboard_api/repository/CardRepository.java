package com.guilherme.flowboard_api.repository;

import com.guilherme.flowboard_api.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByBoardColumnIdOrderByPosition(Long boardColumnId);
}