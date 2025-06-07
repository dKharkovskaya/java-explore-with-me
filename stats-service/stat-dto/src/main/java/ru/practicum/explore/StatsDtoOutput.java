package ru.practicum.explore;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatsDtoOutput {
    private String app;
    private String uri;
    private Long hits;
}
