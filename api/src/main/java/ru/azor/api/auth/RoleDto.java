package ru.azor.api.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Модель ролей пользователя")
public class RoleDto implements Serializable {
    private final static long serialVersionUID = 4388770317028352629L;
    @Schema(description = "ID роли пользователя", required = true, example = "1")
    private Long id;
    @Schema(description = "Имя роли пользователя", required = true, example = "ROLE_USER")
    private String name;
}
