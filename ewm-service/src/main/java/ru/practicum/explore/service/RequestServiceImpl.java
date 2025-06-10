package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.dto.request.ParticipationRequestDto;
import ru.practicum.explore.enums.RequestState;
import ru.practicum.explore.error.exception.ConflictException;
import ru.practicum.explore.error.exception.NotFoundException;
import ru.practicum.explore.mapper.RequestMapper;
import ru.practicum.explore.model.Event;
import ru.practicum.explore.model.Request;
import ru.practicum.explore.model.User;
import ru.practicum.explore.repository.EventRepository;
import ru.practicum.explore.repository.RequestRepository;
import ru.practicum.explore.repository.UserRepository;

import java.util.*;

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

        if (Objects.equals(userId, event.getInitiator().getId())) {
            throw new ConflictException("initiator can non make request on own event");
        } else if (event.getState() != RequestState.PUBLISHED) {
            throw new ConflictException("participation in not publicised event is forbidden");
        } else if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Participation limit on event reached");
        }

        Request participationRequest = new Request();
        participationRequest.setRequester(user);
        participationRequest.setEvent(event);
        RequestState status = (!event.getRequestModeration() || event.getParticipantLimit() == 0) ?
                RequestState.CONFIRMED : RequestState.PENDING;
        participationRequest.setStatus(status.toString());
        //participationRequest.setCreated(Instant.now());

        participationRequest = requestRepository.save(participationRequest);
        if (status == RequestState.CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        return RequestMapper.toDto(participationRequest);
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
