package ru.practicum.explore.service;

import ru.practicum.explore.StatsDtoInput;
import ru.practicum.explore.StatsDtoOutput;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    StatsDtoInput hit(StatsDtoInput statsDtoInput);

    List<StatsDtoOutput> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

}
