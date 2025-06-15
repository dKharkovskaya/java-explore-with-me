package ru.practicum.explore.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {
    private Long id;

    @Size(min = 1, message = "Минимальная длина поля 1 символов")
    @Size(max = 50, message = "Максимальная длина поля 50 символов")
    @NotNull(message = "Поле не может быть неопределенным")
    @NotEmpty(message = "Поле не может быть пустым")
    @NotBlank(message = "Поле не может состоять из пробелов")
    private String name;
}
