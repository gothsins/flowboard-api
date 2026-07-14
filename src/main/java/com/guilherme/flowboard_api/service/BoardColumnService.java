package com.guilherme.flowboard_api.service;

import com.guilherme.flowboard_api.dto.BoardColumnRequest;
import com.guilherme.flowboard_api.dto.BoardColumnResponse;
import com.guilherme.flowboard_api.entity.Board;
import com.guilherme.flowboard_api.entity.BoardColumn;
import com.guilherme.flowboard_api.repository.BoardColumnRepository;
import com.guilherme.flowboard_api.repository.BoardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardColumnService {

    private final BoardColumnRepository boardColumnRepository;
    private final BoardRepository boardRepository;

    public BoardColumnResponse createColumn(Long boardId, BoardColumnRequest request, Long userId) {
        Board board = findBoardOrThrow(boardId);
        assertIsMember(board, userId);

        int nextPosition = boardColumnRepository.findByBoardIdOrderByPosition(boardId).size();

        BoardColumn column = BoardColumn.builder()
                .title(request.getTitle())
                .position(nextPosition)
                .board(board)
                .build();

        return new BoardColumnResponse(boardColumnRepository.save(column));
    }

    public List<BoardColumnResponse> listColumns(Long boardId, Long userId) {
        Board board = findBoardOrThrow(boardId);
        assertIsMember(board, userId);

        return boardColumnRepository.findByBoardIdOrderByPosition(boardId).stream()
                .map(BoardColumnResponse::new)
                .toList();
    }

    public BoardColumnResponse updateColumn(Long columnId, BoardColumnRequest request, Long userId) {
        BoardColumn column = findColumnOrThrow(columnId);
        assertIsMember(column.getBoard(), userId);

        column.setTitle(request.getTitle());
        return new BoardColumnResponse(column);
    }

    public void deleteColumn(Long columnId, Long userId) {
        BoardColumn column = findColumnOrThrow(columnId);
        assertIsMember(column.getBoard(), userId);
        boardColumnRepository.delete(column);
    }

    private BoardColumn findColumnOrThrow(Long columnId) {
        return boardColumnRepository.findById(columnId)
                .orElseThrow(() -> new IllegalArgumentException("Column not found"));
    }

    private Board findBoardOrThrow(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));
    }

    private void assertIsMember(Board board, Long userId) {
        boolean isMember = board.getMembers().stream()
                .anyMatch(member -> member.getId().equals(userId));
        if (!isMember) {
            throw new AccessDeniedException("You are not a member of this board");
        }
    }
}