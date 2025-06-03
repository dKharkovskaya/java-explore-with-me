package ru.practicum.explore.service;

import ru.practicum.explore.StatsDtoInput;
import ru.practicum.explore.StatsDtoOutput;
import ru.practicum.explore.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    Stats hit(Stats stats);

    List<StatsDtoOutput> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

}
