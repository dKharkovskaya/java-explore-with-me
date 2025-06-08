package ru.practicum.explore.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.explore.dto.category.CategoryDto;
import ru.practicum.explore.dto.user.UserShortDto;

import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {
    private Long id;
    private String title;
    private String annotation;
    private Boolean paid;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private CategoryDto category;
    private Integer confirmedRequests;
    private Integer views;
}
