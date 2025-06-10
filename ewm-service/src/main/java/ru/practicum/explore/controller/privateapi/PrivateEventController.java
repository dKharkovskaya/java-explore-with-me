package ru.practicum.explore.controller.privateapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.event.EventFullDto;
import ru.practicum.explore.dto.event.EventShortDto;
import ru.practicum.explore.dto.event.NewEventDto;
import ru.practicum.explore.dto.event.UpdateEventUserRequest;
import ru.practicum.explore.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explore.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explore.dto.request.ParticipationRequestDto;
import ru.practicum.explore.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/events")
@Validated
public class PrivateEventController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable @Positive Long userId,
                                 @RequestBody @Valid NewEventDto newEventDto) {
        return eventService.addEvent(userId, newEventDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getUserEvents(@PathVariable @Positive Long userId,
                                             @RequestParam(name = "from", required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(name = "size", required = false, defaultValue = "10") @Positive Integer size) {
        return eventService.getUserEvents(userId, from, size);
    }

    @GetMapping("/{event-id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getUserEventById(@PathVariable @Positive Long userId,
                                         @PathVariable("event-id") @Positive Long eventId) {
        return eventService.getUserEventById(userId, eventId);
    }

    @PatchMapping("/{event-id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable @Positive Long userId,
                                    @PathVariable("event-id") @Positive Long eventId,
                                    @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        return eventService.updateEventByUser(userId, eventId, updateEventUserRequest);
    }

    @PatchMapping("/{event-id}/requests")
    public EventRequestStatusUpdateResult updateEventRequestsPrivate(@PathVariable @Positive Long userId,
                                                                     @PathVariable("event-id") @Positive Long eventId,
                                                                     @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return eventService.updateEventRequestsPrivate(userId, eventId, eventRequestStatusUpdateRequest);
    }

    @GetMapping("/{event-id}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getParticipationRequestsOnEvent(@PathVariable @Positive Long userId,
                                                                         @PathVariable("event-id") @Positive Long eventId) {
        return eventService.getRequestsOnEvent(userId, eventId);
    }

}