package ru.practicum.explore.service;

import ru.practicum.explore.dto.user.UserDto;
import ru.practicum.explore.dto.user.NewUserRequest;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto dto);

    void deleteUser(Long userId);

    List<UserDto> getUsers(List<Long> ids, int from, int size);
}
