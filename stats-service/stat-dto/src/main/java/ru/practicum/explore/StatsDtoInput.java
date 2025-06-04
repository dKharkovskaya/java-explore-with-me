package ru.practicum.explore;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsDtoInput {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @NotBlank(message = "App name cannot be blank")
    private String app;

    @NotBlank(message = "URI cannot be blank")
    private String uri;

    @NotBlank(message = "IP address cannot be blank")
    private String ip;

    @NotBlank(message = "Timestamp cannot be blank")
    private String timestamp; // формат: "yyyy-MM-dd HH:mm:ss"
}
