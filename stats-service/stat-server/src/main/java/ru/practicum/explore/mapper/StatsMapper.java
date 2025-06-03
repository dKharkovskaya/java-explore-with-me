package ru.practicum.explore.mapper;


import lombok.experimental.UtilityClass;
import ru.practicum.explore.StatsDtoInput;
import ru.practicum.explore.model.Stats;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class StatsMapper {

    public static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Stats toStats(StatsDtoInput statsDtoInput) {
        Stats stats = new Stats();
        stats.setId(statsDtoInput.getId());
        stats.setApp(statsDtoInput.getApp());
        stats.setUri(statsDtoInput.getUri());
        stats.setIp(statsDtoInput.getIp());
        stats.setTimestamp(LocalDateTime.parse(URLDecoder
                .decode(statsDtoInput.getTimestamp(), StandardCharsets.UTF_8), FORMAT));
        return stats;
    }

}

