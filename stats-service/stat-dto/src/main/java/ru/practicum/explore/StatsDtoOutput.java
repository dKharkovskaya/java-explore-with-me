package ru.practicum.explore;

import lombok.*;

@Data
@AllArgsConstructor
public class StatsDtoOutput {
    private final String app;
    private final String uri;
    private final Long hits;
}
