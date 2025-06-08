package ru.practicum.explore.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.explore.dto.Location;
import ru.practicum.explore.dto.user.UserShortDto;
import ru.practicum.explore.dto.category.CategoryDto;

import java.time.LocalDateTime;

import lombok.*;
import ru.practicum.explore.enums.RequestState;

@Data
@Builder
public class EventFullDto {
    private Long id;
    private String title;
    private String annotation;
    private CategoryDto category;
    private Boolean paid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private UserShortDto initiator;
    private Integer views;
    private Integer confirmedRequests;
    private String description;
    private Integer participantLimit;
    private RequestState state;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    private Location location;

    private Boolean requestModeration;
}
