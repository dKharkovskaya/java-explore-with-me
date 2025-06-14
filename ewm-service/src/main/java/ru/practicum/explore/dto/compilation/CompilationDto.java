package ru.practicum.explore.dto.compilation;

import java.util.List;

import lombok.*;
import ru.practicum.explore.dto.event.EventShortDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDto {
    private Long id;
    private List<EventShortDto> events;
    private String title;
    private Boolean pinned;
}
