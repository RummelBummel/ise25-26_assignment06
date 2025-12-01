package de.seuhd.campuscoffee.domain.model;

import lombok.Builder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Builder(toBuilder = true)
public record User(
        @Nullable Long id,
        @Nullable Instant createdAt,
        @Nullable Instant updatedAt,

        @NonNull String loginName,
        @NonNull String emailAddress,
        @NonNull String firstName,
        @NonNull String lastName
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public User {
        loginName = Objects.requireNonNull(loginName, "loginName must not be null");
        emailAddress = Objects.requireNonNull(emailAddress, "emailAddress must not be null");
        firstName = Objects.requireNonNull(firstName, "firstName must not be null");
        lastName = Objects.requireNonNull(lastName, "lastName must not be null");
    }
}
