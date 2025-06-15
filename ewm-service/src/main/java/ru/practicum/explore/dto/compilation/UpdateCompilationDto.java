package ru.practicum.explore.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCompilationDto {
    private Long id;
    private List<Long> events;
    private Boolean pinned;
    @Size(min = 1, max = 50)
    private String title;
}
