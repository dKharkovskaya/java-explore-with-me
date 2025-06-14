package ru.practicum.explore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.StatsDtoInput;
import ru.practicum.explore.StatsDtoOutput;
import ru.practicum.explore.error.exception.BadRequest;
import ru.practicum.explore.mapper.StatsMapper;
import ru.practicum.explore.model.Stats;
import ru.practicum.explore.service.StatsService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class StatsController {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public Stats hit(@RequestBody @Valid StatsDtoInput dto) {
        Stats stats = StatsMapper.toStats(dto);
        return statsService.hit(stats);
    }

    @GetMapping("/stats")
    public List<StatsDtoOutput> getStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {
        if (start.isAfter(end)) {
            throw new BadRequest("Параметр start должен быть раньше end");
        }
        if (uris == null || uris.isEmpty()) {
            return unique ?
                    statsService.getStatsUnique(start, end) :
                    statsService.getStatsAll(start, end);
        } else {
            return uris.stream()
                    .map(uri -> statsService.getStatsByUri(start, end, uri, unique))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .sorted(Comparator.comparing(StatsDtoOutput::getHits).reversed())
                    .toList();
        }
    }
}
