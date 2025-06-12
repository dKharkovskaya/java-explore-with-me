package ru.practicum.explore.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.explore.dto.user.UserShortDto;
import ru.practicum.explore.dto.category.CategoryDto;

import java.time.LocalDateTime;

import lombok.*;
import ru.practicum.explore.enums.RequestState;
import ru.practicum.explore.model.Location;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    private Long id;

    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private RequestState state;

    private String title;
}
