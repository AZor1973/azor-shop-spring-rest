package ru.azor.api.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Модель запрса/ответа")
public class StringResponseRequestDto {
    @Schema(description = "Служебное строковое поле", required = true, example = "Example")
    private String value;
    @Schema(description = "Служебное строковое поле", required = true, example = "Example")
    private String token;
    @Schema(description = "Служебное строковое поле", required = true, example = "Example")
    private String username;
    @Schema(description = "Служебное строковое поле", required = true, example = "Example")
    private String password;
    @Schema(description = "HttpStatus", required = true, example = "HttpStatus.OK")
    private HttpStatus httpStatus;
    @Schema(description = "Служебный список", required = true, example = "{Example1, Example2}")
    private List<?> list;
}
