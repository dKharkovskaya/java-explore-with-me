package ru.practicum.explore.mapper;


import ru.practicum.explore.dto.request.RequestDto;
import ru.practicum.explore.model.Request;

public class RequestMapper {

    public static RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .requester(request.getRequester().getId())
                .created(request.getCreated())
                .status(request.getStatus())
                .event(request.getEvent().getId())
                .build();
    }
}
