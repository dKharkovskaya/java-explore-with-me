package ru.practicum.explore.dto.event;

import ru.practicum.explore.dto.Location;

import java.time.LocalDateTime;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventUserRequest {
    private String annotation;
    private Long category;
    private String description;
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String stateAction;
    private String title;
}
