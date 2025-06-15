package ru.practicum.explore.service;

import ru.practicum.explore.dto.comment.CommentInputDto;
import ru.practicum.explore.dto.comment.CommentShortDto;
import ru.practicum.explore.model.Comment;

import java.util.List;

public interface CommentService {
    void deleteComment(Long commentId);

    Comment getCommentById(Long commentId);

    List<CommentShortDto> getAllCommentsByEventId(Long eventId);

    CommentShortDto createComment(Long userId, Long eventId, CommentInputDto commentInputDto);

    CommentShortDto updateComment(Long userId, Long commentId, CommentInputDto commentDto);

    void deleteCommentByUser(Long userId, Long commentId);
}
