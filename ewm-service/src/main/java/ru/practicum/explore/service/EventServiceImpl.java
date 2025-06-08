package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.explore.StatsDtoInput;
import ru.practicum.explore.StatsDtoOutput;
import ru.practicum.explore.dto.category.CategoryDto;
import ru.practicum.explore.dto.event.*;
import ru.practicum.explore.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explore.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explore.dto.user.UserShortDto;
import ru.practicum.explore.enums.RequestState;
import ru.practicum.explore.enums.StateAction;
import ru.practicum.explore.error.exception.BadRequestException;
import ru.practicum.explore.error.exception.ConflictException;
import ru.practicum.explore.error.exception.NotFoundException;
import ru.practicum.explore.mapper.CategoryMapper;
import ru.practicum.explore.mapper.EventMapper;
import ru.practicum.explore.mapper.UserMapper;
import ru.practicum.explore.model.Event;
import ru.practicum.explore.model.User;
import ru.practicum.explore.repository.CategoryRepository;
import ru.practicum.explore.repository.EventRepository;
import ru.practicum.explore.repository.RequestRepository;
import ru.practicum.explore.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с идентификатором " + userId));
        Event event = EventMapper.fromNewEventDto(dto);
        event.setInitiator(userId);
        if (LocalDateTime.parse(dto.getEventDate(), formatter).isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Необходимо соблюдать минимальный интервал времени в 2 часа до начала события");
        }
        if (dto.getPaid() == null) {
            event.setPaid(false);
        }
        if (dto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
        if (dto.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }
        event.setCreatedOn(LocalDateTime.now());
        event.setState(RequestState.PENDING);
        event.setPublishedOn(null);
        event = eventRepository.save(event);

        Long catId = event.getCategory();
        CategoryDto categoryDto = CategoryMapper.toDto(categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Не найдена категория с идентификатором" + catId)));
        UserShortDto userDto = UserMapper.toShortDto(user);
        return EventMapper.toEventFullDto(event, categoryDto, userDto);
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с идентификатором " + userId));
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return eventRepository.getEventsPrivate(userId, pageRequest).stream()
                .map(event -> {
                    Long catId = event.getCategory();
                    CategoryDto categoryDto = CategoryMapper.toDto(categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Не найдена категория с идентификатором" + catId)));
                    UserShortDto userDto = UserMapper.toShortDto(user);
                    EventShortDto eventDto = EventMapper.toEventShortDto(event);
                    eventDto.setInitiator(userDto);
                    eventDto.setCategory(categoryDto);
                    return eventDto;
                }).toList();
    }

    @Override
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с идентификатором " + userId));
        Event event = eventRepository.getEventPrivate(userId, eventId);
        if (event == null) {
            throw new NotFoundException("Не найдено событие с идентификатором " + eventId);
        }
        Long catId = event.getCategory();
        CategoryDto categoryDto = CategoryMapper.toDto(categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Не найдена категория с идентификатором " + catId)));
        UserShortDto userDto = UserMapper.toShortDto(user);
        return EventMapper.toEventFullDto(event, categoryDto, userDto);
    }

    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с идентификатором " + userId));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Не найдено событие с идентификатором " + eventId));
        if (event.getState() != RequestState.PENDING && event.getState() != RequestState.CANCELED) {
            throw new ConflictException("Изменять можно только события в статусах: PENDING, CANCELED");
        }
        if (!event.getInitiator().equals(userId)) {
            throw new ConflictException("У пользователя " + userId + " нет доступа к событию " + eventId);
        }
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            event.setCategory(dto.getCategory());
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            LocalDateTime date = dto.getEventDate();
            if (date.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("Необходимо соблюдать минимальный интервал времени в 2 часа до начала события");
            }
            event.setEventDate(date);
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
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        if (dto.getLocation() != null) {
            event.setLocationLat(dto.getLocation().getLat());
            event.setLocationLon(dto.getLocation().getLon());
        }
        if (dto.getStateAction() != null && dto.getStateAction() == StateAction.SEND_TO_REVIEW) {
            event.setState(RequestState.PENDING);
        }
        if (dto.getStateAction() != null && dto.getStateAction() == StateAction.CANCEL_REVIEW) {
            event.setState(RequestState.CANCELED);
        }
        event = eventRepository.save(event);

        Long catId = event.getCategory();
        CategoryDto categoryDto = CategoryMapper.toDto(categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Не найдена категория с идентификатором" + catId)));
        UserShortDto userDto = UserMapper.toShortDto(user);
        return EventMapper.toEventFullDto(event, categoryDto, userDto);
    }

    @Transactional
    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));

        // EventMapper.updateEventFromAdminDto(event, dto);
        // return EventMapper.toEventFullDto(eventRepository.save(event));
        throw new RuntimeException();
    }

    @Override
    public List<EventFullDto> getAdminEvents(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        LocalDateTime dateFrom = null;
        if (rangeStart != null) {
            dateFrom = rangeStart;
        }
        LocalDateTime dateTo = null;
        if (rangeEnd != null) {
            dateTo = rangeEnd;
        }
        if (dateFrom != null && dateTo != null && dateTo.isBefore(dateFrom)) {
            throw new BadRequestException("Интервал фильтрации задан некорректно");
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        return eventRepository.getEventsAdmin(users, categories, states, dateFrom, dateTo, pageable).stream()
                .map(event -> {
                    if (event.getConfirmedRequests() == null) {
                        int confirmed = requestRepository.countByEventAndStatus(event.getId(), RequestState.CONFIRMED);
                        event.setConfirmedRequests(confirmed);
                    }
                    Long catId = event.getCategory();
                    Long userId = event.getInitiator();
                    CategoryDto categoryDto = CategoryMapper.toDto(categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Не найдена категория с идентификатором" + catId)));
                    UserShortDto initiatorDto = UserMapper.toShortDto(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с идентификатором " + userId)));
                    return EventMapper.toEventFullDto(event, categoryDto, initiatorDto);
                })
                .toList();
    }

    @Override
    public List<EventFullDto> getPublicEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, int from, int size, String uri, Object request) {

        LocalDateTime dateFrom = null;
        if (rangeStart != null) {
            dateFrom = rangeStart;
        }
        LocalDateTime dateTo = null;
        if (rangeEnd != null) {
            dateTo = rangeEnd;
        }
        if (rangeStart == null && rangeEnd == null) {
            dateFrom = LocalDateTime.now();
        }
        if (dateFrom != null && dateTo != null && dateTo.isBefore(dateFrom)) {
            throw new BadRequestException("Диапазон дат задан неверно");
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        List<EventFullDto> resp = null;
        if (sort.equals("EVENT_DATE")) {
            resp = eventRepository.getEventsPublicOrderByEventDate(text, categories, paid, onlyAvailable, dateFrom, dateTo, pageable).stream()
                    .map(event -> {
                        Long catId = event.getCategory();
                        CategoryDto categoryDto = CategoryMapper.toDto(categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Не найдена категория с идентификатором" + catId)));
                        Long userId = event.getInitiator();
                        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с идентификатором " + userId));
                        UserShortDto userDto = UserMapper.toShortDto(user);
                        return EventMapper.toEventFullDto(event, categoryDto, userDto);
                    })
                    .toList();
        } else {
            resp = eventRepository.getEventsPublicOrderByViews(text, categories, paid, onlyAvailable, dateFrom, dateTo, pageable).stream()
                    .map(event -> {
                        Long catId = event.getCategory();
                        CategoryDto categoryDto = CategoryMapper.toDto(categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Не найдена категория с идентификатором" + catId)));
                        Long userId = event.getInitiator();
                        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с идентификатором " + userId));
                        UserShortDto userDto = UserMapper.toShortDto(user);
                        return EventMapper.toEventFullDto(event, categoryDto, userDto);
                    })
                    .toList();
        }
        return resp;
    }

    @Override
    public EventFullDto getPublicEventById(Long id, String uri, Object request) {
        Event event = getEventByIdAndCheckPublished(id);
        statsClient.hit(new StatsDtoInput("ewm-main-server", uri, ((HttpServletRequest) request).getRemoteAddr(), LocalDateTime.now().format(StatsDtoInput.DATE_TIME_FORMATTER)));
        //EventFullDto dto = EventMapper.toEventFullDto(event);
        //return dto;
        throw new RuntimeException();
    }

    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));
    }

    private Long getViews(Long eventId) {
        List<StatsDtoOutput> stats = statsClient.getStats(LocalDateTime.now().minusYears(10), LocalDateTime.now().plusYears(10), List.of("/events/" + eventId), true);
        return stats.isEmpty() ? 0 : stats.get(0).getHits();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateEventRequestsPrivate(Long userId, Long eventId, EventRequestStatusUpdateRequest entity) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с идентификатором " + userId));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Не найдено событие с идентификатором " + eventId));
        throw new ConflictException("Заявку может изменить только инициатор");
    }
}
