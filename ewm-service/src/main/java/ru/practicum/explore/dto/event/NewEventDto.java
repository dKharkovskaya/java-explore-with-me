package ru.practicum.explore.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.explore.dto.Location;

import java.time.LocalDateTime;

import lombok.*;

import javax.validation.constraints.*;

@Data
@Builder
public class NewEventDto {
    @Size(min = 20, message = "Минимальная длина поля 20 символов")
    @Size(max = 2000, message = "Максимальная длина поля 2000 символов")
    @NotNull(message = "Поле не может быть неопределенным")
    @NotEmpty(message = "Поле не может быть пустым")
    @NotBlank(message = "Поле не может состоять из пробелов")
    private String annotation;

    @Size(min = 20, message = "Минимальная длина поля 20 символов")
    @Size(max = 7000, message = "Максимальная длина поля 7000 символов")
    @NotNull(message = "Поле не может быть неопределенным")
    @NotEmpty(message = "Поле не может быть пустым")
    @NotBlank(message = "Поле не может состоять из пробелов")
    private String description;

    @Size(min = 3, message = "Минимальная длина поля 3 символов")
    @Size(max = 120, message = "Максимальная длина поля 120 символов")
    @NotNull(message = "Поле не может быть неопределенным")
    @NotEmpty(message = "Поле не может быть пустым")
    @NotBlank(message = "Поле не может состоять из пробелов")
    private String title;

    @NotNull(message = "Поле не может быть неопределенным")
    private String eventDate;

    @NotNull(message = "Поле не может быть неопределенным")
    private Long category;

    private Boolean paid = false;

    @PositiveOrZero
    private Integer participantLimit = 0;

    private Boolean requestModeration = true;

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    private Location location;
}
