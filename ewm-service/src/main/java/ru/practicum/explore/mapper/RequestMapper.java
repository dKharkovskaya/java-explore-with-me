package ru.practicum.explore.mapper;


import ru.practicum.explore.dto.request.ParticipationRequestDto;
import ru.practicum.explore.model.Request;

public class RequestMapper {

    public static ParticipationRequestDto toDto(Request request) {
        return ParticipationRequestDto.builder()
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .id(request.getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }
}
