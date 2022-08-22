package ru.azor.api.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.azor.api.enums.AccountStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Модель профиля пользователя")
public class ProfileDto {
    @Schema(description = "ID пользователя", required = true, example = "1")
    private Long id;
    @Schema(description = "Имя пользователя", required = true, example = "Иван")
    private String firstname;
    @Schema(description = "Фамилия пользователя", required = true, example = "Иванов")
    private String lastname;
    @Schema(description = "Ник пользователя", required = true, example = "Туз")
    private String username;
    @Schema(description = "Пароль пользователя", required = true, example = "Rg345678")
    private String password;
    @Schema(description = "Почта пользователя", required = true, example = "example@gmail.com")
    private String email;
    @Schema(description = "Телефон пользователя", required = true, example = "12345678")
    private String phone;
    @NotNull(message = "Должен быть выбран статус")
    @Schema(description = "Статус пользователя", required = true, example = "NOT_ACTIVE")
    private AccountStatus status;
    @NotEmpty(message = "Должна быть выбрана хотя бы одна роль")
    @Schema(description = "Роли пользователя", required = true, example = "{'ROLE_USER'}")
    private Set<RoleDto> rolesDto;
}
