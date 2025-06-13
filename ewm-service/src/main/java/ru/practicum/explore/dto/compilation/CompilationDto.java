package ru.practicum.explore.dto.compilation;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.practicum.explore.dto.event.EventShortDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDto {
    private Long id;
    private List<EventShortDto> events;
    @NotBlank
    private String title;
    private Boolean pinned;

}
