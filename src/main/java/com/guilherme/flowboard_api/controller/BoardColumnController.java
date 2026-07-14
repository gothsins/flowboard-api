package com.guilherme.flowboard_api.controller;

import com.guilherme.flowboard_api.dto.BoardColumnRequest;
import com.guilherme.flowboard_api.dto.BoardColumnResponse;
import com.guilherme.flowboard_api.security.UserDetailsImpl;
import com.guilherme.flowboard_api.service.BoardColumnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardColumnController {

    private final BoardColumnService boardColumnService;

    @PostMapping("/boards/{boardId}/columns")
    public ResponseEntity<BoardColumnResponse> create(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardColumnRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(boardColumnService.createColumn(boardId, request, userDetails.getId()));
    }

    @GetMapping("/boards/{boardId}/columns")
    public ResponseEntity<List<BoardColumnResponse>> list(
            @PathVariable Long boardId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(boardColumnService.listColumns(boardId, userDetails.getId()));
    }

    @PutMapping("/columns/{columnId}")
    public ResponseEntity<BoardColumnResponse> update(
            @PathVariable Long columnId,
            @Valid @RequestBody BoardColumnRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(boardColumnService.updateColumn(columnId, request, userDetails.getId()));
    }

    @DeleteMapping("/columns/{columnId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long columnId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        boardColumnService.deleteColumn(columnId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}