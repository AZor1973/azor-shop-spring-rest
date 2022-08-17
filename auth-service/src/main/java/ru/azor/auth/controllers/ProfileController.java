package ru.azor.auth.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.azor.api.auth.ProfileDto;
import ru.azor.api.exceptions.ClientException;
import ru.azor.auth.converters.UserConverter;
import ru.azor.auth.entities.User;
import ru.azor.auth.services.RoleService;
import ru.azor.auth.services.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
@Tag(name = "Профиль пользователя", description = "Методы работы с профилем пользователя")
public class ProfileController {
    private final UserService userService;
    private final UserConverter userConverter;

    @Operation(
            summary = "Запрос на получение всех профилей пользователей",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = List.class))
                    )
            }
    )
    @GetMapping
    public List<ProfileDto> getAllUsersInfo(){
        return userService.findAllUsers().stream().map(userConverter::userToProfileDto)
                .collect(Collectors.toList());
    }

    @Operation(
            summary = "Запрос на получение профиля пользователя",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = ProfileDto.class))
                    )
            }
    )
    @GetMapping("/{username}")
    public ProfileDto getCurrentUserInfo(@PathVariable @Parameter(description = "Имя пользователя", required = true) String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ClientException("Пользователь с ником " + username + " не найден.", HttpStatus.NOT_FOUND));
        return userConverter.userToProfileDto(user);
    }

    @Operation(
            summary = "Запрос на изменение профиля пользователя",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200"
                    )
            }
    )
    @PutMapping()
    public ResponseEntity<?> updateUser(@RequestBody @Parameter(description = "Изменённый пользователь", required = true) ProfileDto profileDto){
        userService.updateUserStatusAndRoles(profileDto);
        return new ResponseEntity<>(userConverter.profileDtoToUser(profileDto), HttpStatus.OK);
    }
}