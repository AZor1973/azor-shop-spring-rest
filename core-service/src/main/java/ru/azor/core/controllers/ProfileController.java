package ru.azor.core.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.azor.api.common.StringResponseRequestDto;
import ru.azor.api.core.ProfileDto;

@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "Профиль пользователя", description = "Методы работы с профилем пользователя")
public class ProfileController {

    @Operation(
            summary = "Запрос на получение профиля пользователя",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = ProfileDto.class))
                    )
            }
    )
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ProfileDto getCurrentUserInfo(@RequestHeader @Parameter(description = "Имя пользователя", required = true) String username) {
        return new ProfileDto(username);
    }
}
