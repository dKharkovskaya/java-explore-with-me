package ru.practicum.explore;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class StatsDtoInput {
    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    @NotBlank
    private String ip;
    @NotBlank
    private String timestamp;
}
