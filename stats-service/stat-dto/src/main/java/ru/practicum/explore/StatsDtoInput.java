package ru.practicum.explore;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class StatsDtoInput {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
