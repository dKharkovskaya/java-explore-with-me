package ru.practicum.explore.dto.compilation;

import java.util.List;

import lombok.*;
import ru.practicum.explore.model.Event;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDto {
    private Long id;
    private List<Event> events;
    private String title;
    private Boolean pinned;
}
