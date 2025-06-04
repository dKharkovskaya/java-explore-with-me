package ru.practicum.explore.service;

import ru.practicum.explore.StatsDtoOutput;
import ru.practicum.explore.model.Stats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StatsService {

    Stats hit(Stats stats);

    List<StatsDtoOutput> getStatsUnique(LocalDateTime start, LocalDateTime end);

    List<StatsDtoOutput> getStatsAll(LocalDateTime start, LocalDateTime end);

    Optional<StatsDtoOutput> getStatsByUri(LocalDateTime start, LocalDateTime end, String uri, Boolean unique);

}
