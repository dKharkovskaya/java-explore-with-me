package ru.practicum.explore;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsDtoOutput {
    private  String app;
    private  String uri;
    private  Long hits;
}
