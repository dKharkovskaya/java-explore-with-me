package ru.practicum.explore.dto.event;

import ru.practicum.explore.dto.Location;
import ru.practicum.explore.dto.user.UserShortDto;
import ru.practicum.explore.dto.category.CategoryDto;

import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {
    private Long id;
    private String title;
    private String annotation;
    private CategoryDto category;
    private Boolean paid;
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private String description;
    private Integer participantLimit;
    private String state;
    private LocalDateTime createdOn;
    private Location location;
    private Boolean requestModeration;
    private Long views;
    private Integer confirmedRequests;
}
