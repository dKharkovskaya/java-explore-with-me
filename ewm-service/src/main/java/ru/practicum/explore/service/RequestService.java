package ru.practicum.explore.service;

import ru.practicum.explore.dto.request.RequestDto;

import java.util.List;

public interface RequestService {

    RequestDto createRequest(Long userId, Long eventId);

    List<RequestDto> getUserRequests(Long userId);

    RequestDto cancelRequest(Long userId, Long requestId);

}
