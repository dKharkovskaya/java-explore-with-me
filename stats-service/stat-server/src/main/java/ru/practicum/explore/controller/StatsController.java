package ru.practicum.explore.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.StatsDtoInput;
import ru.practicum.explore.StatsDtoOutput;
import ru.practicum.explore.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@Slf4j
public class StatsController {

    public static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final StatsService service;

    public StatsController(StatsService service) {
        this.service = service;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public StatsDtoInput hit(@RequestBody @Valid StatsDtoInput statsDtoInput) {
        return service.hit(statsDtoInput);
    }

    @GetMapping("/stats")
    public List<StatsDtoOutput> getStats(@RequestParam @DateTimeFormat(pattern = FORMAT) LocalDateTime start,
                                         @RequestParam @DateTimeFormat(pattern = FORMAT) LocalDateTime end,
                                         @RequestParam(required = false) List<String> uris,
                                         @RequestParam(defaultValue = "false") Boolean unique) {
        return service.getStats(start, end, uris, unique);
    }
}
