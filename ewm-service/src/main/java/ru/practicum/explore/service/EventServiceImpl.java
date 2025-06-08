package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.explore.StatsDtoInput;
import ru.practicum.explore.StatsDtoOutput;
import ru.practicum.explore.dto.event.*;
import ru.practicum.explore.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explore.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explore.enums.RequestState;
import ru.practicum.explore.error.exception.ConflictException;
import ru.practicum.explore.error.exception.NotFoundException;
import ru.practicum.explore.mapper.EventMapper;
import ru.practicum.explore.mapper.RequestMapper;
import ru.practicum.explore.model.Category;
import ru.practicum.explore.model.Event;
import ru.practicum.explore.model.Request;
import ru.practicum.explore.model.User;
import ru.practicum.explore.repository.CategoryRepository;
import ru.practicum.explore.repository.EventRepository;
import ru.practicum.explore.repository.RequestRepository;
import ru.practicum.explore.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Category category = categoryRepository.findById(dto.getCategory()).orElseThrow(() -> new NotFoundException("Category with id=" + dto.getCategory() + " was not found"));

        Event event = EventMapper.toEvent(dto, user, category);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiatorId(userId, page).stream().map(EventMapper::toEventShortDto).toList();
    }

    @Override
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new NotFoundException("Event not found or not owned by user"));
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest dto) {
        Event event = getEventIfExists(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event not found or user is not the initiator");
        }

        if (!"PENDING".equals(event.getState()) && !"CANCELED".equals(event.getState())) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        // Обновляем поля
        if (dto.getAnnotation() != null) event.setAnnotation(dto.getAnnotation());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getEventDate() != null) event.setEventDate(dto.getEventDate());
        if (dto.getCategory() != null) event.setCategory(getCategoryIfExists(dto.getCategory()));
        if (dto.getPaid() != null) event.setPaid(dto.getPaid());
        if (dto.getParticipantLimit() != null) event.setParticipantLimit(dto.getParticipantLimit());
        if (dto.getRequestModeration() != null) event.setRequestModeration(dto.getRequestModeration());

        // Сохраняем
        Event updated = eventRepository.save(event);
        EventFullDto fullDto = EventMapper.toEventFullDto(updated);

        // Получаем количество просмотров
        //fullDto.setViews(statsClient.getViews(fullDto.getId()));

        return fullDto;
    }

    @Transactional
    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));

        EventMapper.updateEventFromAdminDto(event, dto);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventFullDto> getAdminEvents(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        List<Event> events = eventRepository.findByFilters(users, states, categories, rangeStart, rangeEnd, PageRequest.of(from / size, size));
        return events.stream().map(e -> {
            EventFullDto dto = EventMapper.toEventFullDto(e);
            dto.setViews(getViews(e.getId()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, int from, int size, String uri, Object request) {

        if (rangeStart == null) rangeStart = LocalDateTime.now().plusHours(2); // по умолчанию будущие события
        if (rangeEnd == null) rangeEnd = LocalDateTime.now().plusYears(10);

        List<Event> events = eventRepository.findPublishedEventsWithFilters(text, categories, paid, rangeStart, rangeEnd);
        /*if ("VIEWS".equals(sort)) {
            // Загружаем просмотры для каждого события
            List<String> uris = events.stream()
                    .map(e -> "/events/" + e.getId())
                    .toList();

            List<StatsDtoOutput> statsList = statsClient.getStats(rangeStart, rangeEnd, uris, true);

            Map<String, Long> uriToViews = statsList.stream()
                    .collect(Collectors.toMap(StatsDtoOutput::getUri, StatsDtoOutput::getHits));

            events.sort(Comparator.comparing(
                    e -> uriToViews.getOrDefault("/events/" + e.getId(), 0L),
                    Comparator.nullsLast(Long::compareTo)
            ).reversed());
        } else {
            events.sort(Comparator.comparing(Event::getEventDate));
        }*/

        // Логируем в статистику
        List<String> uris = events.stream().map(e -> "/events/" + e.getId()).toList();

        statsClient.hit(new StatsDtoInput("ewm-main-server", uri, ((HttpServletRequest) request).getRemoteAddr(), LocalDateTime.now().format(StatsDtoInput.DATE_TIME_FORMATTER)));

        List<StatsDtoOutput> statsList = statsClient.getStats(LocalDateTime.now().minusYears(10), LocalDateTime.now().plusYears(10), uris, true);

        for (Event event : events) {
            Optional<StatsDtoOutput> stat = statsList.stream().filter(s -> s.getApp().equals("ewm-main-server") && s.getUri().endsWith("/events/" + event.getId())).findFirst();
            event.setViews(stat.map(StatsDtoOutput::getHits).orElse(0L));
        }

        return events.stream().filter(e -> !onlyAvailable || e.getConfirmedRequests() < e.getParticipantLimit()).sorted(sort == null ? Comparator.comparing(Event::getEventDate) : Comparator.comparing(Event::getViews).reversed()).skip(from).limit(size).map(e -> {
            EventShortDto dto = EventMapper.toEventShortDto(e);
            //dto.se(getConfirmedRequests(e.getId()));
            //dto.setViews(e.getViews());
            return dto;
        }).toList();
    }

    @Override
    public EventFullDto getPublicEventById(Long id, String uri, Object request) {
        Event event = getEventByIdAndCheckPublished(id);
        statsClient.hit(new StatsDtoInput("ewm-main-server", uri, ((HttpServletRequest) request).getRemoteAddr(), LocalDateTime.now().format(StatsDtoInput.DATE_TIME_FORMATTER)));
        EventFullDto dto = EventMapper.toEventFullDto(event);
        dto.setViews(getViews(id));
        return dto;
    }

    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));
    }

    private Long getConfirmedRequests(Long eventId) {
        return eventRepository.findConfirmedRequests(eventId);
    }

    private Long getViews(Long eventId) {
        List<StatsDtoOutput> stats = statsClient.getStats(LocalDateTime.now().minusYears(10), LocalDateTime.now().plusYears(10), List.of("/events/" + eventId), true);
        return stats.isEmpty() ? 0 : stats.get(0).getHits();
    }

    private Event getEventIfExists(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    private Category getCategoryIfExists(Long eventId) {
        return categoryRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateEventRequestsPrivate(Long userId, Long eventId, EventRequestStatusUpdateRequest entity) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с идентификатором " + userId));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Не найдено событие с идентификатором " + eventId));
        if (!event.getInitiator().equals(userId)) {
            throw new ConflictException("Заявку может изменить только инициатор");
        }
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new ConflictException("Нет необходимости подтверждать эту заявку");
        }
        List<Request> requests = requestRepository.findByIdIn(entity.getRequestIds());
        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();
        int confirmedCount = requestRepository.countByEventAndStatus(eventId, RequestState.CONFIRMED);

        for (Request req : requests) {
            if (!req.getStatus().equals(RequestState.PENDING)) {
                throw new ConflictException("Обновить можно только заявки в статусе PENDING");
            }
        }
        for (Request req : requests) {
            if (RequestState.CONFIRMED.toString() == req.getStatus()) {
                if (event.getParticipantLimit() != 0 && confirmedCount >= event.getParticipantLimit()) {
                    req.setStatus(RequestState.REJECTED.toString());
                    rejected.add(req);
                } else {
                    req.setStatus(RequestState.CONFIRMED.toString());
                    confirmed.add(req);
                    confirmedCount++;
                }
            } else if (entity.getStatus() == RequestState.REJECTED.toString()) {
                req.setStatus(RequestState.REJECTED.toString());
                rejected.add(req);
            }
        }
        requestRepository.saveAll(requests);

        if (entity.getStatus() == RequestState.CONFIRMED.toString() && event.getParticipantLimit() != 0 && confirmedCount >= event.getParticipantLimit()) {
            List<Request> pending = requestRepository.findByEventAndStatus(eventId, RequestState.PENDING);
            for (Request req : pending) {
                req.setStatus(RequestState.REJECTED.toString());
            }
            requestRepository.saveAll(pending);
            rejected.addAll(pending);
        }
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmed.stream().map(RequestMapper::toDto).collect(Collectors.toList()))
                .rejectedRequests(rejected.stream().map(RequestMapper::toDto).collect(Collectors.toList()))
                .build();
    }
}
