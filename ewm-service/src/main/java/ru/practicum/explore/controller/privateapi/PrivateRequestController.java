package ru.practicum.explore.controller.privateapi;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explore.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explore.dto.request.ParticipationRequestDto;
import ru.practicum.explore.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateRequestController {

    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        return requestService.getUserRequests(userId);
    }

    @PostMapping("/{eventId}")
    public ParticipationRequestDto addRequest(@PathVariable Long userId, @PathVariable Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestsStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest dto) {
        return requestService.updateRequestsStatus(userId, eventId, dto);
    }
}
