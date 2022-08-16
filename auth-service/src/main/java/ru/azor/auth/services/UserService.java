package ru.azor.auth.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import ru.azor.api.auth.UserDto;
import ru.azor.api.common.StringResponseRequestDto;
import ru.azor.api.enums.AccountStatus;
import ru.azor.api.exceptions.ClientException;
import ru.azor.auth.converters.UserConverter;
import ru.azor.auth.entities.Role;
import ru.azor.auth.entities.User;
import ru.azor.auth.repositories.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final MailService mailService;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(String.format("User '%s' not found", username)));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public StringResponseRequestDto presave(UserDto userDto, BindingResult bindingResult) {
        String response;
        HttpStatus httpStatus;
        int code = 0;
        List<String> errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        if (bindingResult.hasErrors()) {
            response = String.join(" ,", errors);
            httpStatus = HttpStatus.BAD_REQUEST;
            log.error("Ошибка ввода данных при регистрации");
        } else if (isUsernamePresent(userDto.getUsername())) {
            response = "Пользователь с таким именем уже существует";
            httpStatus = HttpStatus.CONFLICT;
            log.error("Пользователь с таким именем уже существует");
        } else if (isEmailPresent(userDto.getEmail())) {
            response = "Пользователь с таким адресом электронной почты уже существует";
            httpStatus = HttpStatus.CONFLICT;
            log.error("Пользователь с таким адресом электронной почты уже существует");
        } else {
            save(userConverter.dtoToEntity(userDto));
            code = mailService.sendMail(userDto.getEmail());
            log.info("Код подтверждения выслан на почту " + userDto.getEmail());
            response = "Новый пользователь создан";
            httpStatus = HttpStatus.CREATED;
            log.info("Новый пользователь создан");
        }
        return StringResponseRequestDto.builder().value(response)
                .password(String.valueOf(code))
                .httpStatus(httpStatus).build();
    }

    public Boolean isUsernamePresent(String username) {
        return userRepository.isUsernamePresent(username) > 0;
    }

    public Boolean isEmailPresent(String email) {
        return userRepository.isEmailPresent(email) > 0;
    }

    @Transactional
    public void activateUser(String username) {
        userRepository.updateUserStatusByUsername(AccountStatus.ACTIVE, username);
        userRepository.updateUserEnabled(true, username);
        log.info("Новый пользователь активирован");
    }

    @Transactional
    public User updateUserStatusAndRoles(Long userId, AccountStatus status, Set<Role> roles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ClientException("Пользователь с идентификатором " + userId + " не найден."));
        user.setStatus(status);
        user.setRoles(roles);
        return user;
    }
}