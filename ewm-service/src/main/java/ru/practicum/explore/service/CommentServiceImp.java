package ru.practicum.explore.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explore.dto.comment.CommentInputDto;
import ru.practicum.explore.dto.comment.CommentShortDto;
import ru.practicum.explore.error.exception.BadRequestException;
import ru.practicum.explore.error.exception.NotFoundException;
import ru.practicum.explore.mapper.CommentMapper;
import ru.practicum.explore.model.Comment;
import ru.practicum.explore.model.Event;
import ru.practicum.explore.model.User;
import ru.practicum.explore.repository.CommentRepository;
import ru.practicum.explore.repository.EventRepository;
import ru.practicum.explore.repository.UserRepository;

import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImp implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        commentRepository.delete(comment);
    }

    @Override
    public Comment getCommentById(Long comId) {
        Comment comment = commentRepository.findById(comId).orElseThrow(
                () -> new NotFoundException("Комментарий с id=" + comId + " не найден"));
        return comment;
    }

    @Override
    public List<CommentShortDto> getAllCommentsByEventId(Long eventId) {
        List<Comment> comments = commentRepository.findAllByEventId(eventId);
        return comments.stream()
                .map(CommentMapper::commentShortDto)
                .toList();
    }

    @Override
    public CommentShortDto createComment(Long userId, Long eventId, CommentInputDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        Event event = eventRepository.findById(eventId).orElseThrow();
        Comment comment = CommentMapper.toComment(commentDto, user, event);
        if (isNull(comment.getText()) || comment.getText().isBlank()) {
            throw new BadRequestException("Комментарий не должен быть пустым");
        }
        Comment result = commentRepository.save(comment);
        return CommentMapper.commentShortDto(result);
    }

    @Override
    public CommentShortDto updateComment(Long userId, Long commentId, CommentInputDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        if (user.getId().equals(comment.getUser().getId())) {
            comment.setText(commentDto.getText());
            if (comment.getText().isBlank()) {
                throw new BadRequestException("Комментарий не должен быть пустым");
            }
            Comment result = commentRepository.save(comment);
            return CommentMapper.commentShortDto(result);
        } else {
            throw new BadRequestException("Редактировать комментарий может только его создатель");
        }
    }

    @Override
    public void deleteCommentByUser(Long userId, Long commentId) {
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        if (user.getId().equals(comment.getUser().getId())) {
            commentRepository.delete(comment);
        } else {
            throw new BadRequestException("Удалить комментарий может только его создатель");
        }
    }

}
