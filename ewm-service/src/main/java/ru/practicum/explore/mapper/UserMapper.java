package ru.practicum.explore.mapper;

import ru.practicum.explore.dto.user.UserDto;
import ru.practicum.explore.dto.user.UserShortDto;
import ru.practicum.explore.model.User;

public class UserMapper {

    public static User toUser(UserDto dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static UserShortDto toShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}