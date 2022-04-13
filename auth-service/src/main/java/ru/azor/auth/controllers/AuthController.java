package ru.azor.auth.controllers;

import lombok.RequiredArgsConstructor;
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
import ru.azor.api.auth.UserDto;
import ru.azor.auth.services.UserService;
import ru.azor.auth.utils.JwtTokenUtil;

import javax.validation.Valid;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
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
        StringResponseRequestDto response = userService.getStringResponseRequestDto(userDto, bindingResult);
        if (response.getHttpStatus() == HttpStatus.CREATED){
            codes.put(userDto.getUsername(), Integer.parseInt(response.getPassword()));
        }
        return response;
    }

    @PostMapping("/confirm_registration")
    public StringResponseRequestDto confirmRegistration(@RequestBody StringResponseRequestDto inputCode) {
        int code = codes.get(inputCode.getUsername());
        if (code == Integer.parseInt(inputCode.getValue())) {
            userService.activateUser(inputCode.getUsername());

            UserDetails userDetails = userService.loadUserByUsername(inputCode.getUsername());
            String token = jwtTokenUtil.generateToken(userDetails);
            codes.remove(inputCode.getUsername());
            return StringResponseRequestDto.builder().value("OK")
                    .token(token)
                    .httpStatus(HttpStatus.OK)
                    .build();
        }
        return StringResponseRequestDto.builder().value("Неправильный код")
                .httpStatus(HttpStatus.CONFLICT)
                .build();
    }
}
