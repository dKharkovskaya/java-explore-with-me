package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explore.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explore.dto.request.ParticipationRequestDto;
import ru.practicum.explore.error.exception.ConflictException;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        // Проверка: нельзя подать заявку на своё же событие
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator cannot participate in own event");
        }

        // Проверка: событие должно быть опубликовано
        if (!"PUBLISHED".equals(event.getState())) {
            throw new ConflictException("Cannot participate in unpublished event");
        }

        // Проверка: не было ли уже такой заявки
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Request already exists");
        }

        // Проверка: лимит участников
        int confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, "CONFIRMED");
        if (event.getParticipantLimit() != 0 && confirmedRequests >= event.getParticipantLimit()) {
            throw new ConflictException("Participant limit reached");
        }

        Request request = Request.builder()
                .requester(user)
                .event(event)
                .created(LocalDateTime.now())
                .status(event.getRequestModeration() ? "PENDING" : "CONFIRMED")
                .build();

        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        getUserIfExists(userId); // проверяем существование
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Request request = getRequestIfExists(requestId);
        if (!request.getRequester().getId().equals(userId)) {
            throw new NotFoundException("Request not found or not owned by user");
        }
        request.setStatus("CANCELED");
        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsStatus(
            Long userId, Long eventId, EventRequestStatusUpdateRequest dto) {

        Event event = getEventIfExists(eventId);
        List<Request> requests = requestRepository.findAllByIdIn(dto.getRequestIds());

        // Проверка, что заявки принадлежат нужному событию
        if (requests.stream().anyMatch(r -> !r.getEvent().getId().equals(eventId))) {
            throw new ConflictException("Some requests are not for this event");
        }

        // Проверка, что заявки в состоянии PENDING
        if (requests.stream().anyMatch(r -> !"PENDING".equals(r.getStatus()))) {
            throw new ConflictException("Only pending requests can be updated");
        }

        // Если модерация отключена или лимит равен 0, всё подтверждено
        boolean autoApproved = !event.getRequestModeration() || event.getParticipantLimit() == 0;

        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();

        int confirmedCount = requestRepository.countByEventIdAndStatus(eventId, "CONFIRMED");

        for (Request request : requests) {
            if (autoApproved || dto.getStatus().equals("CONFIRMED")) {
                if (event.getParticipantLimit() > 0 && confirmedCount >= event.getParticipantLimit()) {
                    request.setStatus("REJECTED");
                    rejected.add(request);
                } else {
                    request.setStatus("CONFIRMED");
                    confirmed.add(request);
                    confirmedCount++;
                }
            } else if (dto.getStatus().equals("REJECTED")) {
                request.setStatus("REJECTED");
                rejected.add(request);
            }
        }

        List<Request> saved = requestRepository.saveAll(confirmed);
        List<Request> rejectedRequests = requestRepository.saveAll(rejected);

        return new EventRequestStatusUpdateResult(
                saved.stream().map(RequestMapper::toDto).toList(),
                rejectedRequests.stream().map(RequestMapper::toDto).toList()
        );
    }

    // Вспомогательные методы

    private User getUserIfExists(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
    }

    private Event getEventIfExists(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    private Request getRequestIfExists(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));
    }
}
