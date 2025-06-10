package ru.practicum.explore.dto.request;

import java.time.Instant;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationRequestDto {
    private Instant created;
    private Long event;
    private Long id;
    private Long requester;
    private String status;
}
