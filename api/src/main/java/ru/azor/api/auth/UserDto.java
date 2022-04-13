package ru.azor.api.auth;

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
public class UserDto {
    private Long id;
    @NotBlank(message = "Поле имени пользователя не должно быть пустым")
    private String firstname;
    @NotBlank(message = "Поле фамилии пользователя не должно быть пустым")
    private String lastname;
    @NotBlank(message = "Поле ника пользователя не должно быть пустым")
    private String username;
    @NotBlank(message = "Поле пароля пользователя не должно быть пустым")
    @Size(min = 8, message = "Длина пароля должна быть не менее 8 символов")
    private String password;
    @NotBlank(message = "Поле почты пользователя не должно быть пустым")
    @Email(message = "Неправильный формат электронной почты")
    private String email;
    @NotBlank(message = "Поле телефона пользователя не должно быть пустым")
    @Size(min = 5, message = "Длина номера телефона должна быть не менее 5 символов")
    private String phone;
}
