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
import ru.azor.api.dto.StringResponse;
import ru.azor.api.exceptions.AppError;
import ru.azor.auth.converters.UserConverter;
import ru.azor.auth.dto.JwtRequest;
import ru.azor.auth.dto.JwtResponse;
import ru.azor.api.auth.UserDto;
import ru.azor.auth.services.UserService;
import ru.azor.auth.utils.JwtTokenUtil;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final UserConverter userConverter;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppError("AUTH_SERVICE_INCORRECT_USERNAME_OR_PASSWORD", "Incorrect username or password"), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/registration")
    public StringResponse registration(@RequestBody @Valid UserDto userDto, BindingResult bindingResult) {
        StringResponse stringResponse = new StringResponse();
        HttpStatus httpStatus;
        List<String> errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        if (bindingResult.hasErrors()) {
            stringResponse.setValue(String.join(" ,", errors));
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (userService.isUsernamePresent(userDto.getUsername())) {
            stringResponse.setValue("Пользователь с таким именем уже существует");
            httpStatus = HttpStatus.CONFLICT;
        } else {
            userService.save(userConverter.dtoToEntity(userDto));
            stringResponse.setValue("Новый пользователь создан");
            httpStatus = HttpStatus.CREATED;
        }
        String responseString = stringResponse.getValue();
        return new StringResponse(responseString, httpStatus);
    }
}
