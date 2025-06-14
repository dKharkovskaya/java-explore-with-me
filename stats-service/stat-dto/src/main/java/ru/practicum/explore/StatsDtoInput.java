package ru.practicum.explore;

import lombok.*;

import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsDtoInput {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private String app;
    private String uri;
    private String ip;
    private String timestamp; // формат: "yyyy-MM-dd HH:mm:ss"

}
