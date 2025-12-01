package de.seuhd.campuscoffee.api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record UserDto(
        @Nullable Long id,
        @Nullable LocalDateTime createdAt,
        @Nullable LocalDateTime updatedAt,

        @NotNull
        @Size(min = 1, max = 255,
                message = "Login name must be between 1 and 255 characters long.")
        @Pattern(regexp = "\\w+",
                message = "Login name can only contain word characters: [a-zA-Z_0-9]+")
        @NonNull String loginName,

        @NotNull
        @Email
        @NonNull String emailAddress,

        @NotNull
        @Size(min = 1, max = 255)
        @NonNull String firstName,

        @NotNull
        @Size(min = 1, max = 255)
        @NonNull String lastName
) {}
