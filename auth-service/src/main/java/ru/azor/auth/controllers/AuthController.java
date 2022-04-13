package ru.azor.auth.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
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
import ru.azor.api.dto.StringResponseRequestDto;
import ru.azor.api.exceptions.AppError;
import ru.azor.auth.converters.UserConverter;
import ru.azor.api.auth.UserDto;
import ru.azor.auth.services.MailService;
import ru.azor.auth.services.UserService;
import ru.azor.auth.utils.JwtTokenUtil;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final MailService mailService;
    private final UserConverter userConverter;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final Map<String, Integer> codes = new ConcurrentHashMap<>();

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody StringResponseRequestDto authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppError("AUTH_SERVICE_INCORRECT_USERNAME_OR_PASSWORD", "Incorrect username or password"), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtil.generateToken(userDetails);
        codes.remove(authRequest.getUsername());
        return ResponseEntity.ok(StringResponseRequestDto.builder().token(token).build());
    }

    @PostMapping("/registration")
    public StringResponseRequestDto registration(@RequestBody @Valid UserDto userDto, BindingResult bindingResult) {
        String response;
        HttpStatus httpStatus;
        List<String> errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        if (bindingResult.hasErrors()) {
            response = String.join(" ,", errors);
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (userService.isUsernamePresent(userDto.getUsername())) {
            response = "Пользователь с таким именем уже существует";
            httpStatus = HttpStatus.CONFLICT;
        } else if (userService.isEmailPresent(userDto.getEmail())) {
            response = "Пользователь с таким адресом электронной почты уже существует";
            httpStatus = HttpStatus.CONFLICT;
        } else {
            userService.save(userConverter.dtoToEntity(userDto));
            int code = new Random().nextInt(9000) + 1000;
            codes.put(userDto.getUsername(), code);
            mailService.sendMail(userDto.getEmail(), "Подтверждение регистрации", "Код для подтверждения регистрации: " + code);
            response = "Новый пользователь создан";
            httpStatus = HttpStatus.CREATED;
        }
        return StringResponseRequestDto.builder().value(response)
                .httpStatus(httpStatus).build();
    }

    @PostMapping("/confirm_registration")
    public StringResponseRequestDto confirmRegistration(@RequestBody StringResponseRequestDto inputCode) {
        int code = codes.get(inputCode.getUsername());
        if (code == Integer.parseInt(inputCode.getValue())) {
            userService.activateUser(inputCode.getUsername());
            return StringResponseRequestDto.builder().value("OK")
                    .httpStatus(HttpStatus.OK)
                    .build();
        }
        return StringResponseRequestDto.builder().value("Неправильный код")
                .httpStatus(HttpStatus.CONFLICT)
                .build();
    }
}
