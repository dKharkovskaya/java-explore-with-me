package ru.practicum.explore.dto.request;

import java.time.LocalDateTime;

import lombok.*;
import ru.practicum.explore.enums.RequestState;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDto {
    private LocalDateTime created;
    private Long event;
    private Long id;
    private Long requester;
    private RequestState status;
}