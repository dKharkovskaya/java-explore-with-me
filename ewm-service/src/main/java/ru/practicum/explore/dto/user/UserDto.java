package ru.practicum.explore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
}
