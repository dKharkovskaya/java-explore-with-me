package ru.practicum.explore.mapper;

import ru.practicum.explore.StatsDtoInput;
import ru.practicum.explore.model.Stats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatsMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Stats toStats(StatsDtoInput input) {
        return Stats.builder()
                .app(input.getApp())
                .uri(input.getUri())
                .ip(input.getIp())
                .timestamp(LocalDateTime.parse(input.getTimestamp(), StatsDtoInput.DATE_TIME_FORMATTER))
                .build();
    }
}