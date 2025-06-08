package ru.practicum.explore.dto.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @NotBlank
    private String name;

    @NotBlank
    @Email(message = "Invalid email format")
    private String email;
}
