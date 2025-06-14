package ru.practicum.explore.controller.privateapi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.event.*;
import ru.practicum.explore.dto.request.RequestDto;
import ru.practicum.explore.service.EventService;

import java.util.List;

@RequestMapping("/users")
@RestController
@Slf4j
@RequiredArgsConstructor
public class PrivateEventController {

    private final EventService eventService;

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getUsersEvents(@PathVariable @Min(1) Long userId, @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getUsersEvents(userId, from, size);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/events")
    public EventDto createEvent(@PathVariable Long userId, @RequestBody @Valid NewEventDto request) {
        return eventService.createEvent(userId, request);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventDto getFullInformation(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getFullInformation(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventDto updateEvent(@PathVariable Long userId, @PathVariable Long eventId, @RequestBody @Valid EventUpdateUserRequest event) {
        return eventService.updateUsersEvent(userId, eventId, event);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<RequestDto> getInfoAboutRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getInfoAboutRequests(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventStatusUpdateResult changeRequestStatus(@PathVariable Long userId, @PathVariable Long eventId, @RequestBody @Valid EventStatusUpdateRequest request) {
        return eventService.changeRequestStatus(userId, eventId, request);
    }

}