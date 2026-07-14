package com.guilherme.flowboard_api.controller;

import com.guilherme.flowboard_api.dto.CardRequest;
import com.guilherme.flowboard_api.dto.CardResponse;
import com.guilherme.flowboard_api.dto.MoveCardRequest;
import com.guilherme.flowboard_api.security.UserDetailsImpl;
import com.guilherme.flowboard_api.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping("/columns/{columnId}/cards")
    public ResponseEntity<CardResponse> create(
            @PathVariable Long columnId,
            @Valid @RequestBody CardRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(cardService.createCard(columnId, request, userDetails.getId()));
    }

    @GetMapping("/columns/{columnId}/cards")
    public ResponseEntity<List<CardResponse>> list(
            @PathVariable Long columnId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(cardService.listCards(columnId, userDetails.getId()));
    }

    @PutMapping("/cards/{cardId}")
    public ResponseEntity<CardResponse> update(
            @PathVariable Long cardId,
            @Valid @RequestBody CardRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(cardService.updateCard(cardId, request, userDetails.getId()));
    }

    @PatchMapping("/cards/{cardId}/move")
    public ResponseEntity<CardResponse> move(
            @PathVariable Long cardId,
            @Valid @RequestBody MoveCardRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(cardService.moveCard(cardId, request, userDetails.getId()));
    }

    @DeleteMapping("/cards/{cardId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long cardId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        cardService.deleteCard(cardId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}