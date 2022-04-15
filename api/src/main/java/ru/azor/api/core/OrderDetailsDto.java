package ru.azor.api.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailsDto {
    @NotBlank(message = "Поле адреса не должно быть пустым")
    private String address;
    @NotBlank(message = "Поле телефона пользователя не должно быть пустым")
    @Size(min = 5, message = "Длина номера телефона должна быть не менее 5 символов")
    private String phone;
}
