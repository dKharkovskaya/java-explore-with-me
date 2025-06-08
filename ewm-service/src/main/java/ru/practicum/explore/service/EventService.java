package ru.practicum.explore.service;

import ru.practicum.explore.dto.event.*;
import ru.practicum.explore.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explore.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explore.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    // Admin API
    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto);

    List<EventFullDto> getAdminEvents(List<Long> users, List<String> states, List<Long> categories,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    // Private API
    EventFullDto addEvent(Long userId, NewEventDto dto);

    List<EventShortDto> getUserEvents(Long userId, int from, int size);

    EventFullDto getUserEventById(Long userId, Long eventId);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest dto);

    // Public API
    List<EventFullDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                        Boolean onlyAvailable, String sort, int from, int size, String uri, Object request);

    EventFullDto getPublicEventById(Long id, String uri, Object request);

    // Вспомогательные методы
    default Event getEventByIdAndCheckPublished(Long id) {
        Event event = getEventById(id);
        throw new RuntimeException("Event is not published");
    }

    Event getEventById(Long id);

    EventRequestStatusUpdateResult updateEventRequestsPrivate(Long userId, Long eventId, EventRequestStatusUpdateRequest entity);
}
