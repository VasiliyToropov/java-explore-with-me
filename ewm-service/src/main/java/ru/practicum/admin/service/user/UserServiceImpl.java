package ru.practicum.admin.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.admin.model.request.NewUserRequest;
import ru.practicum.admin.model.user.User;
import ru.practicum.admin.repository.UserRepository;
import ru.practicum.exceptions.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User addNewUser(NewUserRequest newUserRequest) {

        User user = new User();
        user.setEmail(newUserRequest.getEmail());
        user.setName(newUserRequest.getName());

        userRepository.save(user);

        log.info("Добавлен пользователь: {}", user.getName());

        return user;
    }

    @Override
    public void deleteUser(Long userId) {

        Optional<User> foundedUserDto = userRepository.findById(userId);

        User user = foundedUserDto.orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        log.info("Удален пользователь с id: {}", userId);

        userRepository.delete(user);
    }

    @Override
    public List<User> getUsers(List<Long> ids, Integer from, Integer size) {

        //учитываются параметры ограничения выборки, либо конкретные пользователи (список ids)
        if (ids == null) {

            // Если from и size не заданы, то устанавливаем значения по умолчанию
            int defaultFrom = (from == null) ? 0 : from;
            int defaultSize = (size == null) ? 10 : size;

            Pageable pageable = PageRequest.of(defaultFrom / defaultSize, defaultSize);

            log.info("Получили список пользователей с выборкой");
            return userRepository.getUsersUsingFromAndSize(pageable);
        } else {
            log.info("Получили список пользователей по их id");
            return userRepository.findByIdIn(ids);
        }


    }

    @Override
    public User getUserById(Long userId) {
        Optional<User> foundedUser = userRepository.findById(userId);

        log.info("Получили пользователя с id: {}", userId);

        return foundedUser.orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
    }
}
