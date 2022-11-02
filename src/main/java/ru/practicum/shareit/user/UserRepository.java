package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long userId = 0L;

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public User addUser(User user) {
        user.setId(updateUserId());
        users.put(user.getId(), user);
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
        users.remove(user.getId());
    }

    private Long updateUserId() {
        return ++userId;
    }

    public boolean contains(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email))
                return true;
        }
        return false;
    }
}
