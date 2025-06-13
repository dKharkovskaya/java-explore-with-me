package ru.practicum.explore.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explore.dto.event.*;
import ru.practicum.explore.enums.State;
import ru.practicum.explore.model.Event;
import ru.practicum.explore.model.Location;
import ru.practicum.explore.model.Request;

import java.util.List;

import static java.util.Objects.isNull;
import static ru.practicum.explore.enums.RequestState.CONFIRMED;

@UtilityClass
public class EventMapper {

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .eventDate(event.getEventDate())
                .paid(event.getPaid())
                .annotation(event.getAnnotation())
                .views(event.getViews())
                .title(event.getTitle())
                .category(CategoryMapper.categoryDto(event.getCategory()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .confirmedRequests(event.getRequests().stream()
                        .filter(request -> request.getStatus().equals(CONFIRMED))
                        .count())
                .build();
    }

    public static EventDto toEventDto(Event event) {
        List<Request> requests = event.getRequests();

        return EventDto.builder()
                .id(event.getId())
                .description(event.getDescription())
                .title(event.getTitle())
                .paid(event.getPaid())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(new Location(event.getLocation().getLat(), event.getLocation().getLon()))
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .state(event.getState())
                .participantLimit(event.getParticipantLimit())
                .eventDate(event.getEventDate())
                .views(event.getViews())
                .category(CategoryMapper.categoryDto(event.getCategory()))
                .requestModeration(event.getRequestModeration())
                .annotation(event.getAnnotation())
                .confirmedRequests(isNull(requests) ? 0 : requests.stream()
                        .filter(request -> CONFIRMED.equals(request.getStatus()))
                        .count())
                .build();
    }

    public static Event toEvent(NewEventDto newEventDto) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .title(newEventDto.getTitle())
                .paid(!isNull(newEventDto.isPaid()) && newEventDto.isPaid())
                .state(State.PENDING)
                .participantLimit(isNull(newEventDto.getParticipantLimit()) ? 0 : newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.isRequestModeration())
                .location(new Location(newEventDto.getLocation().getLon(), newEventDto.getLocation().getLat()))
                .views(0)
                .build();
    }


}
