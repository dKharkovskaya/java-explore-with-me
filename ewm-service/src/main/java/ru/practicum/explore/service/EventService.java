package ru.practicum.explore.service;

import ru.practicum.explore.dto.event.*;
import ru.practicum.explore.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explore.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explore.dto.request.ParticipationRequestDto;

import java.util.List;

public interface EventService {

    List<EventFullDto> getAdminEvents(List<Long> users, List<String> states, List<Long> categories,
                                      String rangeStart, String rangeEnd, int from, int size);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto);

    EventFullDto addEvent(Long userId, NewEventDto dto);

    List<EventShortDto> getUserEvents(Long userId, int from, int size);

    EventFullDto getUserEventById(Long userId, Long eventId);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest dto);

    EventRequestStatusUpdateResult updateEventRequestsPrivate(Long userId, Long eventId, EventRequestStatusUpdateRequest entity);

    List<ParticipationRequestDto> getRequestsOnEvent(Long userId, Long eventId);

    // Public API
    List<EventFullDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                       String rangeStart, String rangeEnd,
                                       Boolean onlyAvailable, String sort, int from, int size, String uri, Object request);

    EventFullDto getPublicEventById(Long id, String uri, Object request);
}
