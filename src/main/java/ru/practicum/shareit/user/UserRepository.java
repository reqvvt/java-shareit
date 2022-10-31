package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final List<User> users = new ArrayList<>();
    private Long userId = 0L;

    public List<User> getAllUsers() {
        return users;
    }

    public Optional<User> getUserById(Long userId) {
        return users.stream()
                    .filter(user -> user.getId().equals(userId))
                    .findAny();
    }

    public User addUser(User user) {
        user.setId(updateUserId());
        users.add(user);
        return user;
    }

    public User updateUser(User user, User oldUser) {
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        return oldUser;
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    private Long updateUserId() {
        return ++userId;
    }

    public boolean contains(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email))
                return true;
        }
        return false;
    }
}
