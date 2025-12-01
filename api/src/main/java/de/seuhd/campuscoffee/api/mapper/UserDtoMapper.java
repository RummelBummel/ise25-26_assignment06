package de.seuhd.campuscoffee.api.mapper;

import de.seuhd.campuscoffee.api.dtos.UserDto;
import de.seuhd.campuscoffee.domain.model.User;
import org.mapstruct.Mapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.jspecify.annotations.Nullable;

@Mapper(componentModel = "spring")
@ConditionalOnMissingBean
public interface UserDtoMapper {

    // --- Hauptmapping ---
    UserDto fromDomain(User source);
    User toDomain(UserDto source);

    // --- Kompatibilität zum UserController ---
    default UserDto toDto(User source) {
        return fromDomain(source);
    }

    default List<UserDto> toDtoList(List<User> users) {
        if (users == null) return List.of();
        return users.stream()
                .map(this::fromDomain)
                .toList();
    }

    // --- Custom timestamps ---

    default @Nullable LocalDateTime map(@Nullable Instant value) {
        if (value == null) return null;
        return LocalDateTime.ofInstant(value, ZoneId.of("UTC"));
    }

    default @Nullable Instant map(@Nullable LocalDateTime value) {
        if (value == null) return null;
        return value.atZone(ZoneId.of("UTC")).toInstant();
    }
}
