package ru.practicum.admin.service.user;

import ru.practicum.admin.model.request.NewUserRequest;
import ru.practicum.admin.model.user.User;

import java.util.List;

public interface UserService {
    public User addNewUser(NewUserRequest newUserRequest);

    public void deleteUser(Long userId);

    public List<User> getUsers(List<Long> ids, Integer from, Integer size);

    public User getUserById(Long userId);
}
