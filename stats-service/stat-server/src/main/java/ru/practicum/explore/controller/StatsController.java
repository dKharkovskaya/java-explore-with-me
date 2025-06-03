package ru.practicum.explore.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.StatsDtoInput;
import ru.practicum.explore.StatsDtoOutput;
import ru.practicum.explore.mapper.StatsMapper;
import ru.practicum.explore.model.Stats;
import ru.practicum.explore.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping
public class StatsController {

    public static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final StatsService service;


    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public Stats hit(@RequestBody @Valid StatsDtoInput dto) {
        return service.hit(StatsMapper.toStats(dto));
    }

    @GetMapping("/stats")
    public List<StatsDtoOutput> getStats(@RequestParam @DateTimeFormat(pattern = FORMAT) LocalDateTime start,
                                         @RequestParam @DateTimeFormat(pattern = FORMAT) LocalDateTime end,
                                         @RequestParam(required = false) List<String> uris,
                                         @RequestParam(defaultValue = "false") Boolean unique) {
        return service.getStats(start, end, uris, unique);
    }
}
