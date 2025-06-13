package ru.practicum.explore.service;

import ru.practicum.explore.dto.user.UserDto;
import ru.practicum.explore.dto.user.NewUserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(NewUserDto userRequest);

    List<UserDto> getAllUsers(List<Long> ids, Integer from, Integer size);

    void deleteUser(Long userId);
}
