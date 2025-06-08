package ru.practicum.explore.dto.user;

import lombok.*;

@Data
@Builder
public class UserShortDto {
    private Long id;
    private String name;
}
