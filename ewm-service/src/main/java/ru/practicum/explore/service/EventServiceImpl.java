package ru.practicum.explore.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;

import ru.practicum.explore.StatsDtoInput;
import ru.practicum.explore.StatsDtoOutput;
import ru.practicum.explore.dto.event.*;
import ru.practicum.explore.dto.request.RequestDto;
import ru.practicum.explore.enums.RequestState;
import ru.practicum.explore.enums.Sort;
import ru.practicum.explore.error.exception.BadRequestException;
import ru.practicum.explore.error.exception.ConflictException;
import ru.practicum.explore.error.exception.NotFoundException;
import ru.practicum.explore.mapper.EventMapper;
import ru.practicum.explore.mapper.RequestMapper;
import ru.practicum.explore.model.*;
import ru.practicum.explore.repository.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Objects.isNull;
import static ru.practicum.explore.enums.RequestState.*;
import static ru.practicum.explore.enums.AdminStateAction.PUBLISH_EVENT;
import static ru.practicum.explore.enums.State.CANCELED;
import static ru.practicum.explore.enums.State.PUBLISHED;
import static ru.practicum.explore.enums.State.PENDING;
import static ru.practicum.explore.enums.StateAction.SEND_TO_REVIEW;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final StatsClient statsBaseClient;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    @Override
    public List<EventDto> getAllEvents(List<Long> users, List<String> states, List<Long> categories,
                                       LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        return eventRepository.getAllEventsByAdmin(users, states, categories, rangeStart, rangeEnd, page).stream()
                .toList().stream()
                .map(EventMapper::toEventDto)
                .toList();
    }

    @Transactional
    @Override
    public EventDto updateEvent(Long eventId, EventUpdateAdminRequest eventUpdateAdminRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        handleAdminRequestData(eventUpdateAdminRequest, event);
        return EventMapper.toEventDto(event);
    }

    @Override
    public List<EventShortDto> getPublicEvents(HttpServletRequest httpRequest, String text, List<Long> categories,
                                               Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                               Sort sort, Integer from, Integer size) {
        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException("Дата окончания поиска не может быть раньше даты начала");
        }
        sendStatistic(httpRequest);
        LocalDateTime start = rangeStart != null ? rangeStart : LocalDateTime.now();
        LocalDateTime end = rangeEnd != null ? rangeEnd : LocalDateTime.now().plusYears(10);
        Pageable page = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.getPublicEvents(text, categories, paid, start, end, page);
        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        if (onlyAvailable) {
            events = events.stream()
                    .filter(event -> event.getParticipantLimit() > event.getParticipantLimit())
                    .toList();
        }
        if (sort != null) {
            validateSort(sort);
            if (sort.equals(Sort.EVENT_DATE)) {
                events.sort(Comparator.comparing(Event::getEventDate));
            } else {
                events.sort(Comparator.comparing(Event::getViews));
            }
        }
        return events.stream()
                .map(EventMapper::toEventShortDto)
                .toList();
    }


    @Override
    @Transactional
    public EventDto getEventById(Long id, HttpServletRequest httpRequest) {
        Event event = eventRepository.findById(id).orElseThrow();
        if (PUBLISHED.equals(event.getState())) {
            sendStatistic(httpRequest);
            getStatistics(List.of(event));
            return EventMapper.toEventDto(event);
        }
        throw new NotFoundException("Не найдено опубликованное событие с id " + id);
    }

    @Override
    public List<EventShortDto> getUsersEvents(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        List<Event> events = eventRepository.findAllByInitiator(user);
        return events.stream()
                .skip(from)
                .limit(size)
                .map(EventMapper::toEventShortDto)
                .toList();
    }

    @Override
    public EventDto createEvent(Long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow();
        Location location = new Location();
        location.setLat(newEventDto.getLocation().getLat());
        location.setLon(newEventDto.getLocation().getLon());
        location = locationRepository.save(location); // сохраняем сначала
        Event event = EventMapper.toEvent(newEventDto);
        if (LocalDateTime.now().isAfter(newEventDto.getEventDate())) {
            throw new NotFoundException("Время события не может быть прошедшее! dateTime: " + newEventDto.getEventDate());
        }
        event.setInitiator(user);
        event.setCategory(category);
        event.setLocation(location);
        Event result = eventRepository.save(event);
        return EventMapper.toEventDto(result);
    }

    @Override
    public EventDto getFullInformation(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        return EventMapper.toEventDto(event);
    }

    @Transactional
    @Override
    public EventDto updateUsersEvent(Long userId, Long eventId, EventUpdateUserRequest eventRequest) {
        userRepository.findById(userId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        if (!event.getState().equals(PUBLISHED)) {
            handleRequestData(eventRequest, event);
        } else {
            throw new ConflictException("Запрос не соответствует требованиям ");
        }
        return EventMapper.toEventDto(event);
    }

    @Override
    public List<RequestDto> getInfoAboutRequests(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        List<Request> requests = event.getRequests();
        return requests.stream()
                .map(RequestMapper::toRequestDto)
                .toList();
    }

    @Transactional
    @Override
    public EventStatusUpdateResult changeRequestStatus(Long userId, Long eventId,
                                                       EventStatusUpdateRequest request) {
        userRepository.findById(userId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        List<Request> requests = event.getRequests().stream()
                .filter(element -> request.getRequestIds().contains(element.getId()))
                .toList();
        List<Request> requests1 = event.getRequests();
        boolean isPending = requests.stream()
                .allMatch(element -> element.getStatus().equals(RequestState.PENDING));
        if (!isPending) {
            throw new ConflictException("В выборке не должно быть запросов кроме статуса PENDING");
        }
        Integer participantLimit = event.getParticipantLimit();
        if (participantLimit == 0 || !event.getRequestModeration()) {
            throw new NotFoundException("Данное событие не требует подтверждения");
        }
        long countConfirmed = event.getRequests().stream()
                .filter(element -> element.getStatus().equals(CONFIRMED))
                .count();
        if (request.getStatus().equals(CONFIRMED)) {
            if ((requests.size() + countConfirmed) <= participantLimit) {
                requests.forEach(element -> element.setStatus(CONFIRMED));

                if ((requests.size() + countConfirmed) == participantLimit) {
                    event.getRequests().stream()
                            .filter(element -> element.getStatus().equals(RequestState.PENDING))
                            .forEach(element -> element.setStatus(RequestState.REJECTED));
                }
                List<Request> result = event.getRequests();
                return EventStatusUpdateResult.builder()
                        .confirmedRequests(result.stream()
                                .filter(element -> element.getStatus().equals(CONFIRMED))
                                .map(RequestMapper::toRequestDto)
                                .toList())
                        .rejectedRequests(result.stream()
                                .filter(element -> element.getStatus().equals(RequestState.REJECTED))
                                .map(RequestMapper::toRequestDto)
                                .toList())
                        .build();
            }
            throw new NotFoundException("Превышен лимит подтвержденных запросов");
        } else if (request.getStatus().equals(RequestState.REJECTED)) {
            requests.forEach((element -> element.setStatus(RequestState.REJECTED)));
            List<Request> result = event.getRequests();
            return EventStatusUpdateResult.builder()
                    .confirmedRequests(result.stream()
                            .filter(element -> element.getStatus().equals(CONFIRMED))
                            .map(RequestMapper::toRequestDto)
                            .toList())
                    .rejectedRequests(result.stream()
                            .filter(element -> element.getStatus().equals(RequestState.REJECTED))
                            .map(RequestMapper::toRequestDto)
                            .toList())
                    .build();
        }
        throw new NotFoundException("Ошибка в статусе запроса");
    }

    private Boolean checkingDateTime(LocalDateTime dateTime) {
        long hours = Duration.between(LocalDateTime.now(), dateTime).toHours();
        return hours >= 2;
    }

    private void handleRequestData(EventUpdateUserRequest request, Event event) {
        if (!isNull(request.getCategory())) {
            Category category = categoryRepository.findById(request.getCategory()).orElseThrow();
            event.setCategory(category);
        }
        if (!isNull(request.getAnnotation())) {
            event.setAnnotation(request.getAnnotation());
        }
        if (!isNull(request.getDescription())) {
            event.setDescription(request.getDescription());
        }
        if (!isNull(request.getEventDate())) {
            if (checkingDateTime(request.getEventDate())) {
                event.setEventDate(request.getEventDate());
            } else {
                throw new NotFoundException("Неверно указано время события");
            }
        }
        if (!isNull(request.getLocation())) {
            event.setLocation(new Location(request.getLocation().getLat(), request.getLocation().getLon()));
        }
        if (!isNull(request.getPaid())) {
            event.setPaid(request.getPaid());
        }
        if (!isNull(request.getParticipantLimit())) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (!isNull(request.getRequestModeration())) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (!isNull(request.getTitle())) {
            event.setTitle(request.getTitle());
        }
        if (!isNull(request.getStateAction())) {
            event.setState(request.getStateAction().equals(SEND_TO_REVIEW) ? PENDING : CANCELED);
        }
    }

    private void handleAdminRequestData(EventUpdateAdminRequest request, Event event) {
        if (!isNull(request.getCategory())) {
            Category category = categoryRepository.findById(request.getCategory()).orElseThrow();
            event.setCategory(category);
        }
        if (!isNull(request.getAnnotation())) {
            event.setAnnotation(request.getAnnotation());
        }
        if (!isNull((request.getTitle()))) {
            event.setTitle(request.getTitle());
        }
        if (!isNull((request.getPaid()))) {
            event.setPaid(request.getPaid());
        }
        if (!isNull(request.getLocation())) {
            Location location = new Location();
            location.setLat(request.getLocation().getLat());
            location.setLon(request.getLocation().getLon());
            location = locationRepository.save(location);
            event.setLocation(location);
        }
        if (!isNull(request.getRequestModeration())) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (!isNull(request.getEventDate())) {
            if (LocalDateTime.now().isAfter(request.getEventDate())) {
                throw new NotFoundException("Время события не может быть прошедшим! dateTime: " + request.getEventDate());
            }
            event.setEventDate(request.getEventDate());
        }
        if (!isNull(request.getDescription())) {
            event.setDescription(request.getDescription());
        }
        if (!isNull(request.getParticipantLimit())) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (!isNull(request.getStateAction())) {
            if (event.getState().equals(PENDING)) {
                event.setState(request.getStateAction().equals(PUBLISH_EVENT) ? PUBLISHED : CANCELED);
            } else {
                throw new ConflictException("Событие уже опубликовано");
            }
        }
    }

    private void sendStatistic(HttpServletRequest httpRequest) {
        StatsDtoInput dto = new StatsDtoInput();
        dto.setApp("ewm-service");
        dto.setUri(httpRequest.getRequestURI());
        dto.setIp(httpRequest.getLocalAddr());
        dto.setTimestamp(LocalDateTime.now().format(StatsDtoInput.DATE_TIME_FORMATTER));
        statsBaseClient.hit(dto);
    }

    public void getStatistics(List<Event> events) {
        if (events == null || events.isEmpty()) return;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<String> uris = events.stream()
                .filter(event -> event.getId() != null)
                .map(event -> String.format("/events/%s", event.getId()))
                .toList();

        if (uris.isEmpty()) return;

        LocalDateTime defaultStart = LocalDateTime.parse("2000-01-01 00:00:00", formatter);
        LocalDateTime defaultEnd = LocalDateTime.parse("2100-01-01 00:00:00", formatter);

        List<StatsDtoOutput> stats = statsBaseClient.getStats(defaultStart, defaultEnd, uris, true);

        for (Event event : events) {
            Optional<StatsDtoOutput> currentViewStats = stats.stream()
                    .filter(dto -> dto.getUri().equals("/events/" + event.getId()))
                    .findFirst();

            Long views = currentViewStats.map(StatsDtoOutput::getHits).orElse(0L);
            event.setViews(views.intValue());
        }
    }

    public void validateSort(Sort sort) {
        if (!Arrays.asList(Sort.values()).contains(sort)) {
            throw new NotFoundException("Такого параметра сортировки не существует");
        }
    }

}

