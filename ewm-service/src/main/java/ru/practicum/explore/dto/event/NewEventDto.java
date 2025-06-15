package ru.practicum.explore.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import ru.practicum.explore.model.Location;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @Size(min = 20, max = 2000)
    @NotBlank
    private String annotation;

    @NotNull
    private Long category;

    @Size(min = 20, max = 7000)
    @NotBlank
    private String description;

    @NotNull
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    @Valid
    private Location location;

    private boolean paid;

    @PositiveOrZero
    private int participantLimit;

    private boolean requestModeration = true;

    @Size(min = 3, max = 120)
    @NotBlank
    private String title;
}
