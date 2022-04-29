package ru.azor.auth.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.azor.api.auth.RoleDto;
import ru.azor.auth.converters.RoleConverter;
import ru.azor.auth.services.RoleService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Роли пользователя", description = "Методы работы с ролями пользователя")
public class RolesController {
    private final RoleService roleService;
    private final RoleConverter roleConverter;

    @Operation(
            summary = "Запрос на получение всех ролей пользователей",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = List.class))
                    )
            }
    )
    @GetMapping
    public List<RoleDto> getAllRoles(){
        return roleService.findAll().stream().map(roleConverter::roleToRoleDto)
                .collect(Collectors.toList());
    }
}
