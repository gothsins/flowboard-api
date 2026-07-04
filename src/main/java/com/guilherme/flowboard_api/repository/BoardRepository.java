package com.guilherme.flowboard_api.repository;

import com.guilherme.flowboard_api.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findByOwnerId(Long ownerId);

    @Query("SELECT b FROM Board b JOIN b.members m WHERE m.id = :userId")
    List<Board> findByMemberId(Long userId);
}