package de.seuhd.campuscoffee.domain.impl;

import de.seuhd.campuscoffee.domain.exceptions.NotFoundException;
import de.seuhd.campuscoffee.domain.model.User;
import de.seuhd.campuscoffee.domain.ports.UserDataService;
import de.seuhd.campuscoffee.domain.ports.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Domain service implementation for user management.
 *
 * Delegates persistence-related operations to the UserDataService port.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final @NonNull UserDataService userDataService;

    @Override
    public @NonNull List<User> getAllUsers() {
        log.debug("Fetching all users");
        // UserDataService garantiert: never null, may be empty
        return userDataService.getAll();
    }

    @Override
    public @NonNull User getUserById(@NonNull Long id) {
        Objects.requireNonNull(id, "id must not be null");
        log.debug("Fetching user with id={}", id);
        // Kann NotFoundException werfen – die lassen wir durchgehen
        return userDataService.getById(id);
    }

    @Override
    public @NonNull List<User> findUsersByLoginName(@NonNull String loginName) {
        Objects.requireNonNull(loginName, "loginName must not be null");
        log.debug("Fetching users with loginName={}", loginName);

        try {
            // Data-Port liefert genau einen User oder wirft NotFoundException
            User user = userDataService.getByLoginName(loginName);
            return List.of(user);
        } catch (NotFoundException e) {
            // Für den Filter-Endpunkt ist „keine Treffer = leere Liste“ sinnvoll
            log.debug("No user found with loginName={}", loginName);
            return List.of();
        }
    }

    @Override
    public @NonNull User createUser(@NonNull User user) {
        Objects.requireNonNull(user, "user must not be null");
        log.debug("Creating new user with loginName={}", user.loginName());

        // ID und Timestamps übernimmt die DB / Data-Layer
        User toCreate = user.toBuilder()
                .id(null)
                .createdAt(null)
                .updatedAt(null)
                .build();

        return userDataService.upsert(toCreate);
    }

    @Override
    public @NonNull User updateUser(@NonNull Long id, @NonNull User user) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(user, "user must not be null");
        log.debug("Updating user with id={}", id);

        // Sicherstellen, dass der User existiert – wirft NotFoundException, falls nicht
        User existing = userDataService.getById(id);

        User updated = existing.toBuilder()
                .loginName(user.loginName())
                .emailAddress(user.emailAddress())
                .firstName(user.firstName())
                .lastName(user.lastName())
                // createdAt aus dem bestehenden User übernehmen
                .createdAt(existing.createdAt())
                // updatedAt wird im Data-Layer neu gesetzt
                .build();

        return userDataService.upsert(updated);
    }

    @Override
    public void deleteUserById(@NonNull Long id) {
        Objects.requireNonNull(id, "id must not be null");
        log.debug("Deleting user with id={}", id);
        // Wir lassen NotFoundException ebenfalls durchlaufen, falls der User nicht existiert
        userDataService.delete(id);
    }
}
