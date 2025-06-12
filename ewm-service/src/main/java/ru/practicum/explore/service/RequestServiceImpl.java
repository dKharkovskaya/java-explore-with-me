package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.dto.request.ParticipationRequestDto;
import ru.practicum.explore.enums.RequestState;
import ru.practicum.explore.error.exception.ConflictException;
import ru.practicum.explore.error.exception.ForbiddenException;
import ru.practicum.explore.error.exception.NotFoundException;
import ru.practicum.explore.mapper.RequestMapper;
import ru.practicum.explore.model.Event;
import ru.practicum.explore.model.Request;
import ru.practicum.explore.model.User;
import ru.practicum.explore.repository.EventRepository;
import ru.practicum.explore.repository.RequestRepository;
import ru.practicum.explore.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static ru.practicum.explore.enums.RequestState.CONFIRMED;
import static ru.practicum.explore.enums.RequestState.PENDING;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = findUserByIdOrThrowNotFoundException(userId);
        Event event = findEventByIdOrThrowNotFoundException(eventId);

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ForbiddenException("Request is already exist.");
        }
        if (userId.equals(event.getInitiator().getId())) {
            throw new ForbiddenException("Initiator can't send request to his own event.");
        }
        if (!event.getState().equals(RequestState.PUBLISHED)) {
            throw new ForbiddenException("Participation is possible only in published event.");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <=
                requestRepository.countByEventIdAndStatus(eventId, CONFIRMED)) {
            throw new ForbiddenException("Participant limit has been reached.");
        }

        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(user);

        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            request.setStatus(String.valueOf(PENDING));
        } else {
            request.setStatus(String.valueOf(CONFIRMED));
        }
        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        findUserByIdOrThrowNotFoundException(userId);
        List<Request> participationRequests = requestRepository.findAllByRequesterId(userId);
        return participationRequests.stream()
                .map(RequestMapper::toDto)
                .toList();
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        findUserByIdOrThrowNotFoundException(userId);
        Request participationRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Объект не найден"));
        if (!Objects.equals(userId, participationRequest.getRequester().getId())) {
            throw new ConflictException("Only initiator can cancel own participation request");
        }
        participationRequest.setStatus(RequestState.CANCELED.toString());
        participationRequest = requestRepository.save(participationRequest);
        return RequestMapper.toDto(participationRequest);
    }

    // Вспомогательные методы

    private User findUserByIdOrThrowNotFoundException(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Объект не найден"));
    }

    private Event findEventByIdOrThrowNotFoundException(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Объект не найден"));
    }
}
