package ru.practicum.explore.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.dto.user.NewUserRequest;
import ru.practicum.explore.dto.user.UserDto;
import ru.practicum.explore.exception.ConflictException;
import ru.practicum.explore.exception.NotFoundException;
import ru.practicum.explore.mapper.UserMapper;
import ru.practicum.explore.model.User;
import ru.practicum.explore.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto addUser(NewUserRequest dto) {
        /*   if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email is already taken");
        }*/

        User user = UserMapper.toUser(dto);
        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        if (ids != null && !ids.isEmpty()) {
            return userRepository.findAllByIdIn(ids).stream()
                    .map(UserMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAll().stream()
                    .skip(from)
                    .limit(size)
                    .map(UserMapper::toDto)
                    .collect(Collectors.toList());
        }
    }
}
