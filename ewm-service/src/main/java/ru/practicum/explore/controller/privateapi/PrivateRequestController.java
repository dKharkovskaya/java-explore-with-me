package ru.practicum.explore.controller.privateapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.request.RequestDto;
import ru.practicum.explore.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class PrivateRequestController {

    private final RequestService requestService;

    @GetMapping("/{userId}/requests")
    public List<RequestDto> getUserRequests(@PathVariable Long userId) {
        return requestService.getUserRequests(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/requests")
    public RequestDto createRequest(@PathVariable Long userId,
                                    @RequestParam Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Long userId,
                                    @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

}
