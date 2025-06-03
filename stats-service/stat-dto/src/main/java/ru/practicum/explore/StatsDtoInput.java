package ru.practicum.explore;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class StatsDtoInput {
    private Long id;
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
