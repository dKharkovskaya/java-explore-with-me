package ru.practicum.explore.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explore.dto.comment.CommentInputDto;
import ru.practicum.explore.dto.comment.CommentShortDto;
import ru.practicum.explore.model.Comment;
import ru.practicum.explore.model.Event;
import ru.practicum.explore.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {

    public static CommentShortDto commentShortDto(Comment comment) {
        return CommentShortDto.builder()
                .text(comment.getText())
                .user(UserMapper.toUserShortDto(comment.getUser()))
                .created(comment.getCreated())
                .build();
    }

    public static Comment toComment(CommentInputDto commentDto, User user, Event event) {
        return Comment.builder()
                .text(commentDto.getText())
                .user(user)
                .event(event)
                .created(LocalDateTime.now())
                .build();
    }
}
