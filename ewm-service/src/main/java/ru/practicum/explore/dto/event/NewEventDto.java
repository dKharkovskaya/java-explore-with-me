package ru.practicum.explore.dto.event;

import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.explore.dto.Location;

import java.time.LocalDateTime;

import lombok.*;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @NotBlank(message = "Аннотация не может быть пустой")
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull(message = "Категория обязательна")
    private Long category;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(min = 20, message = "Описание должно быть не менее 20 символов")
    private String description;

    @Future(message = "Дата события должна быть в будущем")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull(message = "Местоположение обязательно")
    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;
    private String title;
}
