package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.dto.request.RequestDto;
import ru.practicum.explore.enums.RequestState;
import ru.practicum.explore.enums.State;
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
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public List<RequestDto> getUserRequests(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        List<Request> requests = requestRepository.findByRequester(user);
        return requests.stream()
                .map(RequestMapper::toRequestDto)
                .toList();
    }

    @Transactional
    @Override
    public RequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        Event event = eventRepository.findById(eventId).orElseThrow();
        checkRequestData(event, user);
        Request request = Request.builder()
                .requester(user)
                .event(event)
                .status(processStatus(event))
                .build();
        Request result = requestRepository.save(request);
        return RequestMapper.toRequestDto(result);
    }

    @Transactional
    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow();
        Request request = requestRepository.findById(requestId).orElseThrow();
        request.setStatus(RequestState.CANCELED);
        return RequestMapper.toRequestDto(request);
    }

    private RequestState processStatus(Event event) {
        if (!event.getRequestModeration()) {
            return RequestState.CONFIRMED;
        }
        if (event.getParticipantLimit() == 0) {
            return RequestState.CONFIRMED;
        }
        return RequestState.PENDING;
    }

    private void checkRequestData(Event event, User user) {
        long count = event.getRequests().stream()
                .filter(it -> it.getStatus().equals(RequestState.CONFIRMED))
                .count();
        if (user.equals(event.getInitiator())) {
            throw new NotFoundException("Создатель события не может делать запрос");
        } else if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Событие должно быть опубликовано");
        } else if ((event.getParticipantLimit() > 0 && event.getParticipantLimit() <= count)) {
            throw new NotFoundException("Достигнут лимит по заявкам на данное событие");
        }
    }
}
