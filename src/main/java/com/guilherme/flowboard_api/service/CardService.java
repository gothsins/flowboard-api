package com.guilherme.flowboard_api.service;

import com.guilherme.flowboard_api.dto.CardRequest;
import com.guilherme.flowboard_api.dto.CardResponse;
import com.guilherme.flowboard_api.dto.MoveCardRequest;
import com.guilherme.flowboard_api.entity.*;
import com.guilherme.flowboard_api.repository.BoardColumnRepository;
import com.guilherme.flowboard_api.repository.CardRepository;
import com.guilherme.flowboard_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CardService {

    private final CardRepository cardRepository;
    private final BoardColumnRepository boardColumnRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    public CardResponse createCard(Long columnId, CardRequest request, Long userId) {
        BoardColumn column = findColumnOrThrow(columnId);
        Board board = column.getBoard();
        assertIsMember(board, userId);

        User assignee = resolveAssignee(request.getAssigneeId());

        int nextPosition = cardRepository.findByBoardColumnIdOrderByPosition(columnId).size();

        Card card = Card.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .position(nextPosition)
                .boardColumn(column)
                .assignee(assignee)
                .build();

        Card saved = cardRepository.save(card);

        User user = userRepository.findById(userId).orElseThrow();
        activityLogService.log(board, user, ActivityLog.ActionType.CARD_CREATED,
                "Created card '" + saved.getTitle() + "' in column '" + column.getTitle() + "'");

        return new CardResponse(saved);
    }

    public List<CardResponse> listCards(Long columnId, Long userId) {
        BoardColumn column = findColumnOrThrow(columnId);
        assertIsMember(column.getBoard(), userId);

        return cardRepository.findByBoardColumnIdOrderByPosition(columnId).stream()
                .map(CardResponse::new)
                .toList();
    }

    public CardResponse updateCard(Long cardId, CardRequest request, Long userId) {
        Card card = findCardOrThrow(cardId);
        assertIsMember(card.getBoardColumn().getBoard(), userId);

        card.setTitle(request.getTitle());
        card.setDescription(request.getDescription());
        card.setAssignee(resolveAssignee(request.getAssigneeId()));

        return new CardResponse(card);
    }

    public CardResponse moveCard(Long cardId, MoveCardRequest request, Long userId) {
        Card card = findCardOrThrow(cardId);
        Board board = card.getBoardColumn().getBoard();
        assertIsMember(board, userId);

        BoardColumn targetColumn = findColumnOrThrow(request.getTargetColumnId());
        assertIsMember(targetColumn.getBoard(), userId);

        String fromColumn = card.getBoardColumn().getTitle();
        String toColumn = targetColumn.getTitle();

        card.setBoardColumn(targetColumn);
        card.setPosition(request.getNewPosition());

        User user = userRepository.findById(userId).orElseThrow();
        activityLogService.log(board, user, ActivityLog.ActionType.CARD_MOVED,
                "Moved '" + card.getTitle() + "' from '" + fromColumn + "' to '" + toColumn + "'");

        return new CardResponse(card);
    }

    public void deleteCard(Long cardId, Long userId) {
        Card card = findCardOrThrow(cardId);
        assertIsMember(card.getBoardColumn().getBoard(), userId);
        cardRepository.delete(card);
    }

    private User resolveAssignee(Long assigneeId) {
        if (assigneeId == null) return null;
        return userRepository.findById(assigneeId)
                .orElseThrow(() -> new IllegalArgumentException("Assignee not found"));
    }

    private Card findCardOrThrow(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));
    }

    private BoardColumn findColumnOrThrow(Long columnId) {
        return boardColumnRepository.findById(columnId)
                .orElseThrow(() -> new IllegalArgumentException("Column not found"));
    }

    private void assertIsMember(Board board, Long userId) {
        boolean isMember = board.getMembers().stream()
                .anyMatch(member -> member.getId().equals(userId));
        if (!isMember) {
            throw new AccessDeniedException("You are not a member of this board");
        }
    }
}