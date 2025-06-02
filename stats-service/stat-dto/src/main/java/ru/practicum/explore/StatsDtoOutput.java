package ru.practicum.explore;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsDtoOutput {
    private String app;
    private String uri;
    private int hits;
}
