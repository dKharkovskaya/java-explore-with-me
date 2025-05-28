package ru.practicum.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatsDtoOutput {

    String app;
    String uri;
    long hits;
}