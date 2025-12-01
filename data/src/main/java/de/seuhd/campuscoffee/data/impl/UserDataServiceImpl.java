package de.seuhd.campuscoffee.data.impl;

import de.seuhd.campuscoffee.domain.exceptions.DuplicationException;
import de.seuhd.campuscoffee.domain.exceptions.NotFoundException;
import de.seuhd.campuscoffee.domain.model.User;
import de.seuhd.campuscoffee.domain.ports.UserDataService;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Simple in-memory implementation of the user data service.
 * This adapter satisfies the UserDataService port without using a real database.
 *
 * It is sufficient for tests and follows the hexagonal architecture:
 * the domain talks only to the port, not to the storage details.
 */
@Service
@Slf4j
class UserDataServiceImpl implements UserDataService {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1L);

    @Override
    public void clear() {
        log.debug("Clearing all users from in-memory store");
        users.clear();
        idSequence.set(1L);
    }

    @Override
    public @NonNull List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public @NonNull User getById(@NonNull Long id) {
        Objects.requireNonNull(id, "id must not be null");
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException(User.class, id);
        }
        return user;
    }

    @Override
    public @NonNull User getByLoginName(@NonNull String loginName) {
        Objects.requireNonNull(loginName, "loginName must not be null");

        return users.values().stream()
                .filter(u -> u.loginName().equals(loginName))
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException(User.class, "loginName", loginName));
    }

    @Override
    public @NonNull User upsert(@NonNull User user) {
        Objects.requireNonNull(user, "user must not be null");

        // Einzigartigkeits-Constraints prüfen (loginName & emailAddress)
        users.values().forEach(existing -> {
            boolean sameId = user.id() != null && user.id().equals(existing.id());
            if (!sameId) {
                if (existing.loginName().equals(user.loginName())) {
                    throw new DuplicationException(User.class, "loginName", user.loginName());
                }
                if (existing.emailAddress().equals(user.emailAddress())) {
                    throw new DuplicationException(User.class, "emailAddress", user.emailAddress());
                }
            }
        });

        LocalDateTime now = LocalDateTime.now();

        if (user.id() == null) {
            // Neuer User
            long newId = idSequence.getAndIncrement();
            User created = user.toBuilder()
                    .id(newId)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            users.put(newId, created);
            log.debug("Created new user with id={} and loginName={}", newId, created.loginName());
            return created;
        } else {
            // Update eines existierenden Users
            User existing = users.get(user.id());
            if (existing == null) {
                throw new NotFoundException(User.class, user.id());
            }

            User updated = existing.toBuilder()
                    .loginName(user.loginName())
                    .emailAddress(user.emailAddress())
                    .firstName(user.firstName())
                    .lastName(user.lastName())
                    .createdAt(existing.createdAt())
                    .updatedAt(now)
                    .build();

            users.put(updated.id(), updated);
            log.debug("Updated user with id={} and loginName={}", updated.id(), updated.loginName());
            return updated;
        }
    }

    @Override
    public void delete(@NonNull Long id) {
        Objects.requireNonNull(id, "id must not be null");

        if (!users.containsKey(id)) {
            throw new NotFoundException(User.class, id);
        }
        users.remove(id);
        log.debug("Deleted user with id={}", id);
    }
}
