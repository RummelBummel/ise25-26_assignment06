package de.seuhd.campuscoffee.api.mapper;

import de.seuhd.campuscoffee.api.dtos.UserDto;
import de.seuhd.campuscoffee.domain.model.User;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper between User domain model and UserDto.
 */
@Component
public class UserDtoMapper {

    public @NonNull UserDto toDto(@NonNull User user) {
        return UserDto.builder()
                .id(user.id())
                .createdAt(user.createdAt())
                .updatedAt(user.updatedAt())
                .loginName(user.loginName())
                .emailAddress(user.emailAddress())
                .firstName(user.firstName())
                .lastName(user.lastName())
                .build();
    }

    public @NonNull User toDomain(@NonNull UserDto dto) {
        return User.builder()
                .id(dto.id())
                .createdAt(dto.createdAt())
                .updatedAt(dto.updatedAt())
                .loginName(dto.loginName())
                .emailAddress(dto.emailAddress())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .build();
    }

    public @NonNull List<UserDto> toDtoList(@Nullable List<User> users) {
        if (users == null || users.isEmpty()) {
            return List.of();
        }
        return users.stream()
                .map(this::toDto)
                .toList();
    }
}
