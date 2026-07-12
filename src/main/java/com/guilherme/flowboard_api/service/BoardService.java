package com.guilherme.flowboard_api.service;

import com.guilherme.flowboard_api.dto.BoardRequest;
import com.guilherme.flowboard_api.dto.BoardResponse;
import com.guilherme.flowboard_api.entity.Board;
import com.guilherme.flowboard_api.entity.User;
import com.guilherme.flowboard_api.repository.BoardRepository;
import com.guilherme.flowboard_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public BoardResponse createBoard(BoardRequest request, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Board board = Board.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .owner(owner)
                .build();

        board.getMembers().add(owner);

        Board saved = boardRepository.save(board);
        return new BoardResponse(saved);
    }

    public List<BoardResponse> listMyBoards(Long userId) {
        List<Board> owned = boardRepository.findByOwnerId(userId);
        List<Board> memberOf = boardRepository.findByMemberId(userId);

        return Stream.concat(owned.stream(), memberOf.stream())
                .distinct()
                .map(BoardResponse::new)
                .collect(Collectors.toList());
    }

    public BoardResponse getBoard(Long boardId, Long userId) {
        Board board = findBoardOrThrow(boardId);
        assertIsMember(board, userId);
        return new BoardResponse(board);
    }

    public BoardResponse updateBoard(Long boardId, BoardRequest request, Long userId) {
        Board board = findBoardOrThrow(boardId);
        assertIsOwner(board, userId);

        board.setTitle(request.getTitle());
        board.setDescription(request.getDescription());

        return new BoardResponse(board);
    }

    public void deleteBoard(Long boardId, Long userId) {
        Board board = findBoardOrThrow(boardId);
        assertIsOwner(board, userId);
        boardRepository.delete(board);
    }

    public BoardResponse addMember(Long boardId, Long newMemberId, Long requesterId) {
        Board board = findBoardOrThrow(boardId);
        assertIsOwner(board, requesterId);

        User newMember = userRepository.findById(newMemberId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        board.getMembers().add(newMember);
        return new BoardResponse(board);
    }

    public void removeMember(Long boardId, Long memberId, Long requesterId) {
        Board board = findBoardOrThrow(boardId);
        assertIsOwner(board, requesterId);

        if (board.getOwner().getId().equals(memberId)) {
            throw new IllegalArgumentException("Cannot remove the board owner");
        }

        board.getMembers().removeIf(member -> member.getId().equals(memberId));
    }

    private Board findBoardOrThrow(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));
    }

    private void assertIsOwner(Board board, Long userId) {
        if (!board.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Only the board owner can perform this action");
        }
    }

    private void assertIsMember(Board board, Long userId) {
        boolean isMember = board.getMembers().stream()
                .anyMatch(member -> member.getId().equals(userId));
        if (!isMember) {
            throw new AccessDeniedException("You are not a member of this board");
        }
    }
}