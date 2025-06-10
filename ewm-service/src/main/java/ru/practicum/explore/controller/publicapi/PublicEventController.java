package ru.practicum.explore.controller.publicapi;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.StatsClient;
import ru.practicum.explore.dto.event.EventFullDto;
import ru.practicum.explore.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class PublicEventController {

    private final EventService eventService;
    private final StatsClient statisticsClient;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(name = "rangeStart", required = false) String rangeStartString,
            @RequestParam(name = "rangeEnd", required = false) String rangeEndString,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        if (text != null) {
            text = text.equals("0") ? null : text.toLowerCase();
        }
        //statisticsClient.hit(new StatsDtoInput("evm-service", request.getRequestURI(), request.getRemoteAddr()));
        return eventService.getPublicEvents(text, categories, paid, rangeStartString, rangeEndString, onlyAvailable, sort, from, size, request.getRequestURI(), request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable("event-id") @Positive Long eventId,
                                     HttpServletRequest request) {
        String uri = request.getRequestURI();
        long hitsBefore = getEndpointUniqueHits(uri);
        //statisticsClient.hit(new StatsDtoInput("evm-service", uri, request.getRemoteAddr()));
        long hitsAfter = getEndpointUniqueHits(uri);
        boolean uniqueRequest = hitsAfter > hitsBefore;
        return eventService.getPublicEventById(eventId, request.getRequestURI(), request);
    }

    private long getEndpointUniqueHits(String uri) {
        long hits = 0;
        /*Optional<StatsDtoOutput> viewStatsDtoOptional =
                statisticsClient.getStats(Instant.EPOCH, Instant.now(), List.of(uri), true).stream()
                        .filter(viewStatsDto -> viewStatsDto.getApp().equals("evm-service"))
                        .findFirst();
        if (viewStatsDtoOptional.isPresent()) {
            hits = viewStatsDtoOptional.get().getHits();
        }*/
        return hits;
    }

}
