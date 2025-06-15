package ru.practicum.explore.controller.privateapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.comment.CommentInputDto;
import ru.practicum.explore.dto.comment.CommentShortDto;
import ru.practicum.explore.error.exception.NotFoundException;
import ru.practicum.explore.model.Comment;
import ru.practicum.explore.model.User;
import ru.practicum.explore.service.CommentService;

@RequestMapping("/users/{userId}/comments")
@RestController
@RequiredArgsConstructor
@Slf4j
public class PrivateCommentController {
    private final CommentService commentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CommentShortDto createComment(@PathVariable Long userId,
                                         @RequestParam Long eventId,
                                         @RequestBody CommentInputDto commentDto) {
        return commentService.createComment(userId, eventId, commentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentShortDto updateComment(@PathVariable Long userId,
                                         @PathVariable Long commentId,
                                         @RequestBody CommentInputDto commentDto) {
        return commentService.updateComment(userId, commentId, commentDto);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        commentService.deleteCommentByUser(userId, commentId);
    }

}
