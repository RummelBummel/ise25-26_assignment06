package de.seuhd.campuscoffee.api.controller;

import de.seuhd.campuscoffee.api.dtos.UserDto;
import de.seuhd.campuscoffee.api.mapper.UserDtoMapper;
import de.seuhd.campuscoffee.domain.ports.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Users", description = "Operations related to user management.")
@Controller
@RequestMapping("/api/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final @NonNull UserService userService;
    private final @NonNull UserDtoMapper userDtoMapper;

    // GET /api/users
    @GetMapping
    @ResponseBody
    public List<UserDto> getAllUsers() {
        log.debug("HTTP GET /api/users - retrieving all users");
        return userDtoMapper.toDtoList(userService.getAllUsers());
    }

    // GET /api/users/{id}
    @GetMapping("/{id}")
    @ResponseBody
    public UserDto getUserById(@PathVariable("id") Long id) {
        log.debug("HTTP GET /api/users/{} - retrieving user by id", id);
        return userDtoMapper.toDto(userService.getUserById(id));
    }

    // GET /api/users/filter?loginName=foo
    @GetMapping("/filter")
    @ResponseBody
    public List<UserDto> getUsersByLoginName(@RequestParam("loginName") String loginName) {
        log.debug("HTTP GET /api/users/filter?loginName={} - retrieving users by loginName", loginName);
        return userDtoMapper.toDtoList(userService.findUsersByLoginName(loginName));
    }

    // POST /api/users
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.debug("HTTP POST /api/users - creating user with loginName={}", userDto.loginName());
        var createdDomainUser = userService.createUser(userDtoMapper.toDomain(userDto));
        return userDtoMapper.toDto(createdDomainUser);
    }

    // PUT /api/users/{id}
    @PutMapping("/{id}")
    @ResponseBody
    public UserDto updateUser(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserDto userDto
    ) {
        log.debug("HTTP PUT /api/users/{} - updating user", id);
        var updatedDomainUser = userService.updateUser(id, userDtoMapper.toDomain(userDto));
        return userDtoMapper.toDto(updatedDomainUser);
    }

    // DELETE /api/users/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") Long id) {
        log.debug("HTTP DELETE /api/users/{} - deleting user", id);
        userService.deleteUserById(id);
    }
}
