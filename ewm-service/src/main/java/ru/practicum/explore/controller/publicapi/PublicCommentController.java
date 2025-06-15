package ru.practicum.explore.controller.publicapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore.dto.comment.CommentShortDto;
import ru.practicum.explore.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comments")
@Slf4j
@RequiredArgsConstructor
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping("/{eventId}")
    private List<CommentShortDto> getAllCommentsByEventId(@PathVariable Long eventId) {
        return commentService.getAllCommentsByEventId(eventId);
    }
}
