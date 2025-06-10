package ru.practicum.explore.dto.event;

import ru.practicum.explore.dto.category.CategoryDto;
import ru.practicum.explore.dto.user.UserShortDto;

import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
public class EventShortDto {
    Long id;

    String annotation;

    CategoryDto category;

    Long confirmedRequests;

    LocalDateTime eventDate;

    UserShortDto initiator;

    Boolean paid;

    String title;

    Integer views;
}
