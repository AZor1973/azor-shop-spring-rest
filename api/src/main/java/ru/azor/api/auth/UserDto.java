package ru.azor.api.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Модель пользователя")
public class UserDto {
    @Schema(description = "ID пользователя", required = true, example = "1")
    private Long id;
    @Schema(description = "Имя пользователя", required = true, example = "Иван")
    @NotBlank(message = "Поле имени пользователя не должно быть пустым")
    private String firstname;
    @Schema(description = "Фамилия пользователя", required = true, example = "Иванов")
    @NotBlank(message = "Поле фамилии пользователя не должно быть пустым")
    private String lastname;
    @Schema(description = "Ник пользователя", required = true, example = "Туз")
    @NotBlank(message = "Поле ника пользователя не должно быть пустым")
    private String username;
    @Schema(description = "Пароль пользователя", required = true, example = "Rg345678")
    @NotBlank(message = "Поле пароля пользователя не должно быть пустым")
    @Size(min = 8, message = "Длина пароля должна быть не менее 8 символов")
    private String password;
    @Schema(description = "Почта пользователя", required = true, example = "example@gmail.com")
    @NotBlank(message = "Поле почты пользователя не должно быть пустым")
    @Email(message = "Неправильный формат электронной почты")
    private String email;
    @Schema(description = "Телефон пользователя", required = true, example = "12345678")
    @NotBlank(message = "Поле телефона пользователя не должно быть пустым")
    @Size(min = 5, message = "Длина номера телефона должна быть не менее 5 символов")
    private String phone;
}
