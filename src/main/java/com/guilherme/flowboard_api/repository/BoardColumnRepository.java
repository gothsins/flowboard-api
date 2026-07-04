package com.guilherme.flowboard_api.repository;

import com.guilherme.flowboard_api.entity.BoardColumn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardColumnRepository extends JpaRepository<BoardColumn, Long> {
    List<BoardColumn> findByBoardIdOrderByPosition(Long boardId);
}