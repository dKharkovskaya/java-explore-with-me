package ru.practicum.explore.controller.adminapi;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.model.Comment;
import ru.practicum.explore.service.CommentService;

@RestController
@RequestMapping("admin/comments")
@Slf4j
@RequiredArgsConstructor
public class AdminCommentController {
    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }

    @GetMapping("/{commentId}")
    public Comment getCommentById(@PathVariable @NotNull Long commentId) {
        return commentService.getCommentById(commentId);
    }
}
