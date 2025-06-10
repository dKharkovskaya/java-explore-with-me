package ru.practicum.explore.dto.compilation;

import java.util.List;

import lombok.*;
import ru.practicum.explore.dto.event.EventShortDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private Long id;
    private List<EventShortDto> events;
    private String title;
    private Boolean pinned;

    public CompilationDto(Long id, boolean pinned, String title) {
        this.id = id;
        this.pinned = pinned;
        this.title = title;
    }
}
