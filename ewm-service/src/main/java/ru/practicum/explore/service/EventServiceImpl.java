package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;

import ru.practicum.explore.dto.event.*;
import ru.practicum.explore.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explore.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explore.dto.request.ParticipationRequestDto;
import ru.practicum.explore.enums.RequestState;
import ru.practicum.explore.enums.StateActionAdmin;
import ru.practicum.explore.enums.StateActionPrivate;
import ru.practicum.explore.error.exception.ConflictException;
import ru.practicum.explore.error.exception.ForbiddenException;
import ru.practicum.explore.error.exception.NotFoundException;
import ru.practicum.explore.error.exception.ValidationException;
import ru.practicum.explore.mapper.CategoryMapper;
import ru.practicum.explore.mapper.EventMapper;
import ru.practicum.explore.mapper.LocationMapper;
import ru.practicum.explore.mapper.RequestMapper;
import ru.practicum.explore.model.Category;
import ru.practicum.explore.model.Event;
import ru.practicum.explore.model.Location;
import ru.practicum.explore.model.User;
import ru.practicum.explore.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static ru.practicum.explore.enums.RequestState.*;
import static ru.practicum.explore.enums.StateActionAdmin.PUBLISH_EVENT;
import static ru.practicum.explore.enums.StateActionAdmin.REJECT_EVENT;
import static ru.practicum.explore.enums.StateActionPrivate.CANCEL_REVIEW;
import static ru.practicum.explore.enums.StateActionPrivate.SEND_TO_REVIEW;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    final CategoryService categoryService;
    private final StatsClient statsClient;


    @Override
    public List<EventFullDto> getAdminEvents(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, int from, int size) {
        return List.of();
    }


    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto) {
        Event event = getEvent(eventId);
        if (dto.getStateAction() != null) {
            StateActionAdmin stateAction = StateActionAdmin.valueOf(dto.getStateAction());
            if (!event.getState().equals(PENDING) && stateAction.equals(PUBLISH_EVENT)) {
                throw new ForbiddenException("Event can't be published because it's not pending");
            }
            if (event.getState().equals(PUBLISHED) && stateAction.equals(REJECT_EVENT)) {
                throw new ForbiddenException("Event can't be rejected because it's already published.");
            }
            if (stateAction.equals(PUBLISH_EVENT)) {
                event.setState(PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (stateAction.equals(REJECT_EVENT)) {
                event.setState(RequestState.CANCELED);
            }
        }
        String annotation = dto.getAnnotation();
        if (annotation != null && !annotation.isBlank()) {
            event.setAnnotation(annotation);
        }
        if (dto.getCategory() != null) {
            event.setCategory(CategoryMapper.toCategory(categoryService.getCategoryById(dto.getCategory())));
        }
        String description = dto.getDescription();
        if (description != null && !description.isBlank()) {
            event.setDescription(description);
        }
        LocalDateTime eventDate = dto.getEventDate();
        if (eventDate != null) {
            checkActualTime(eventDate);
            event.setEventDate(eventDate);
        }
        if (dto.getLocation() != null) {
            event.setLocationLon(dto.getLocation().getLon());
            event.setLocationLat(dto.getLocation().getLat());
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        String title = dto.getTitle();
        if (title != null && !title.isBlank()) {
            event.setTitle(title);
        }
        return EventMapper.toEventFullDto(eventRepository.save(event),
                requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
    }


    @Override
    public EventFullDto addEvent(Long userId, NewEventDto dto) {
        checkActualTime(dto.getEventDate());
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id=" + userId + " was not found"));
        Long catId = dto.getCategory();
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Category with id=" + catId + " was not found"));
        Location location = checkLocation(LocationMapper.toLocation(dto.getLocation()));
        Event event = EventMapper.toEvent(dto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setLocationLat(location.getLat());
        event.setLocationLon(location.getLon());
        event.setCreatedOn(LocalDateTime.now());
        event.setState(PENDING);
        return EventMapper.toEventFullDto(eventRepository.save(event), 0L);
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        findUserByIdOrThrowNotFoundException(userId);
        Pageable pageable = PageRequest.of(from, size);
        List<Event> eventPage = eventRepository.findAllByInitiatorId(userId, pageable);
        return eventPage.stream()
                .map(EventMapper::toEventShortDto)
                .toList();
    }

    @Override
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        return null;
    }

    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest dto) {
        Event event = getEvent(eventId, userId);
        if (event.getState() == PUBLISHED) {
            throw new ForbiddenException("Published events can't be update");
        }
        String annotation = dto.getAnnotation();
        if (annotation != null && !annotation.isBlank()) {
            event.setAnnotation(annotation);
        }
        if (dto.getCategory() != null) {
            event.setCategory(CategoryMapper.toCategory(categoryService.getCategoryById(dto.getCategory())));
        }
        String description = dto.getDescription();
        if (description != null && !description.isBlank()) {
            event.setDescription(description);
        }
        LocalDateTime eventDate = dto.getEventDate();
        if (eventDate != null) {
            checkActualTime(eventDate);
            event.setEventDate(eventDate);
        }
        if (dto.getLocation() != null) {
            Location location = checkLocation(LocationMapper.toLocation(dto.getLocation()));
            event.setLocation(LocationMapper.toLocationDto(location.getLat(), location.getLon()));
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        String title = dto.getTitle();
        if (title != null && !title.isBlank()) {
            event.setTitle(title);
        }
        if (dto.getStateAction() != null) {
            StateActionPrivate stateActionPrivate = StateActionPrivate.valueOf(dto.getStateAction());
            if (stateActionPrivate.equals(SEND_TO_REVIEW)) {
                event.setState(PENDING);
            } else if (stateActionPrivate.equals(CANCEL_REVIEW)) {
                event.setState(RequestState.CANCELED);
            }
        }
        return EventMapper.toEventFullDto(eventRepository.save(event),
                requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
    }

    @Override
    public EventRequestStatusUpdateResult updateEventRequestsPrivate(Long userId, Long eventId, EventRequestStatusUpdateRequest entity) {
        return null;
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOnEvent(Long userId, Long eventId) {
        findUserByIdOrThrowNotFoundException(userId);
        Event event = findEventByIdOrThrowNotFoundException(eventId);
        if (!Objects.equals(userId, event.getInitiator().getId())) {
            throw new ConflictException(String.format("user id=%d is not initiator of event id=%d", userId, eventId));
        }
        return requestRepository.findAllByEventId(eventId).stream()
                .map(RequestMapper::toDto)
                .toList();
    }


    @Override
    public List<EventFullDto> getPublicEvents(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, int from, int size, String uri, Object request) {
        return List.of();
    }

    @Override
    public EventFullDto getPublicEventById(Long id, String uri, Object request) {
        return null;
    }

    private User findUserByIdOrThrowNotFoundException(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Объект не найден"));
    }

    private Event findEventByIdOrThrowNotFoundException(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Объект не найден"));
    }

    protected void checkActualTime(LocalDateTime eventTime) {
        if (eventTime.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Incorrectly made request.");
        }
    }

    private Location checkLocation(Location location) {
        if (locationRepository.existsByLatAndLon(location.getLat(), location.getLon())) {
            return locationRepository.findByLatAndLon(location.getLat(), location.getLon());
        } else {
            return locationRepository.save(location);
        }
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    private Event getEvent(Long eventId, Long userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));
    }

}
