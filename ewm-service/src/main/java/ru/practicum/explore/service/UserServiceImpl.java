package ru.practicum.explore.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.dto.user.NewUserDto;
import ru.practicum.explore.dto.user.UserDto;
import ru.practicum.explore.error.exception.ConflictException;
import ru.practicum.explore.error.exception.NotFoundException;
import ru.practicum.explore.mapper.UserMapper;
import ru.practicum.explore.model.User;
import ru.practicum.explore.repository.UserRepository;

import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers(List<Long> ids, Integer from, Integer size) {
        if (isNull(ids)) {
            return userRepository.findAll().stream()
                    .skip(from)
                    .limit(size)
                    .map(UserMapper::toUserDto)
                    .toList();
        }
        return userRepository.findAllById(ids).stream()
                .skip(from)
                .limit(size)
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Transactional
    @Override
    public UserDto createUser(NewUserDto userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new ConflictException("Пользователь с почтой '" + userRequest.getEmail() + "' уже существует");
        }
        User user = UserMapper.toUser(userRequest);
        userRepository.save(user);
        return UserMapper.toUserDto(user);
    }


    @Transactional
    @Override
    public void deleteUser(Long userId) {
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            throw new NotFoundException();
        }
        userRepository.deleteById(userId);
    }
}
