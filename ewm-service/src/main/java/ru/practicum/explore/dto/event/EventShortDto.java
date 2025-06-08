package ru.practicum.explore.dto.event;

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
    private LocalDateTime eventDate;
    private UserShortDto initiator;
}
