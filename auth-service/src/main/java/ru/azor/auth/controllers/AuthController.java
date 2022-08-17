package ru.azor.auth.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.azor.api.common.StringResponseRequestDto;
import ru.azor.api.auth.UserDto;
import ru.azor.api.exceptions.AppError;
import ru.azor.auth.services.UserService;
import ru.azor.auth.utils.JwtTokenUtil;

import javax.validation.Valid;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Методы работы с аутентификацией и регистрацией пользователей")
public class AuthController {
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final Map<String, Integer> codes = new ConcurrentHashMap<>();

    @Operation(
            summary = "Создание токена",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "201",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class))
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "401",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class))
                    )
            }
    )
    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody @Parameter(description = "Credentials пользователя", required = true) StringResponseRequestDto authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            log.error("Incorrect username or password");
            return new ResponseEntity<>(new AppError("Incorrect username or password"), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtil.generateToken(userDetails);
        codes.remove(authRequest.getUsername());
        log.info(authRequest.getUsername() + " logged in");
        return ResponseEntity.ok(StringResponseRequestDto.builder().token(token)
                .list(jwtTokenUtil.getRoles(token))
                .build());
    }

    @Operation(
            summary = "Регистрация нового пользователя",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "201",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class))
                    ),
                    @ApiResponse(
                            description = "Совпадение ника пользователя или почты", responseCode = "409",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class))
                    ),
                    @ApiResponse(
                            description = "Ошибка ввода данных", responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class))
                    )
            }
    )
    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody @Valid @Parameter(description = "Регистрирующийся пользователь", required = true) UserDto userDto,
                                          @Parameter(description = "Вводимые данные", required = true) BindingResult bindingResult) {
        StringResponseRequestDto response = userService.presave(userDto, bindingResult);
        if (response.getHttpStatus() == HttpStatus.CREATED) {
            codes.put(userDto.getUsername(), Integer.parseInt(response.getPassword()));
        }
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(
            summary = "Подтверждение регистрации нового пользователя",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class))
                    ),
                    @ApiResponse(
                            description = "Ошибка подтверждения регистрации", responseCode = "409",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class))
                    )
            }
    )
    @PostMapping("/confirm_registration")
    public ResponseEntity<?> confirmRegistration(@RequestBody @Parameter(description = "Код подтверждения регистрации и имя пользователя", required = true) StringResponseRequestDto inputCode) {
        int code = codes.get(inputCode.getUsername());
        if (code == Integer.parseInt(inputCode.getValue())) {
            userService.activateUser(inputCode.getUsername());

            UserDetails userDetails = userService.loadUserByUsername(inputCode.getUsername());
            String token = jwtTokenUtil.generateToken(userDetails);
            codes.remove(inputCode.getUsername());
            return new ResponseEntity<>(StringResponseRequestDto.builder().value("OK")
                    .token(token)
                    .build(), HttpStatus.OK);
        }
        log.error("Неправильный код");
        return new ResponseEntity<>(StringResponseRequestDto.builder().value("Неправильный код")
                .build(), HttpStatus.CONFLICT);
    }
}
