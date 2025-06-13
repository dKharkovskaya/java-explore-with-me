package ru.practicum.explore.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.explore.dto.event.*;
import ru.practicum.explore.dto.request.RequestDto;
import ru.practicum.explore.enums.Sort;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventDto> getAllEvents(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart,
                                LocalDateTime rangeEnd, Integer from, Integer size);

    EventDto updateEvent(Long eventId, EventUpdateAdminRequest eventUpdateAdminRequest);

    List<EventShortDto> getPublicEvents(HttpServletRequest httpRequest, String text, List<Long> categories,
                                        Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                        Sort sort, Integer from, Integer size);

    EventDto getEventById(Long id, HttpServletRequest httpRequest);

    List<EventShortDto> getUsersEvents(Long userId, Integer from, Integer size);

    EventDto createEvent(Long userId, NewEventDto event);

    EventDto getFullInformation(Long userId, Long eventId);

    EventDto updateUsersEvent(Long userId, Long eventId, EventUpdateUserRequest event);

    List<RequestDto> getInfoAboutRequests(Long userId, Long eventId);

    EventStatusUpdateResult changeRequestStatus(Long userId, Long eventId,
                                                EventStatusUpdateRequest request);
}
