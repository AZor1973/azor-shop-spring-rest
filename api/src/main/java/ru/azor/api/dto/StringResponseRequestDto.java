package ru.azor.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StringResponseRequestDto {
    private String value;
    private String token;
    private String username;
    private String password;
    private HttpStatus httpStatus;
}
