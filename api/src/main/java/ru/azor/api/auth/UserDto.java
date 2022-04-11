package ru.azor.api.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "Поле имени пользователя не должно быть пустым")
    private String username;
    @Size(min = 8, message = "Длина пароля должна быть не менее 8 символов")
    private String password;
    @Email(message = "Неправильный формат электронной почты")
    private String email;
}
