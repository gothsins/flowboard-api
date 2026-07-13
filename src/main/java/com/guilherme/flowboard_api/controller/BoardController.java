package com.guilherme.flowboard_api.controller;

import com.guilherme.flowboard_api.dto.BoardRequest;
import com.guilherme.flowboard_api.dto.BoardResponse;
import com.guilherme.flowboard_api.security.UserDetailsImpl;
import com.guilherme.flowboard_api.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardResponse> create(
            @Valid @RequestBody BoardRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        BoardResponse response = boardService.createBoard(request, userDetails.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BoardResponse>> listMine(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(boardService.listMyBoards(userDetails.getId()));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponse> get(
            @PathVariable Long boardId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(boardService.getBoard(boardId, userDetails.getId()));
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<BoardResponse> update(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(boardService.updateBoard(boardId, request, userDetails.getId()));
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long boardId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        boardService.deleteBoard(boardId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{boardId}/members/{userId}")
    public ResponseEntity<BoardResponse> addMember(
            @PathVariable Long boardId,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(boardService.addMember(boardId, userId, userDetails.getId()));
    }

    @DeleteMapping("/{boardId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long boardId,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        boardService.removeMember(boardId, userId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}