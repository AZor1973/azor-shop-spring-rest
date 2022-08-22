package ru.azor.auth.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import ru.azor.api.auth.ProfileDto;
import ru.azor.api.auth.UserDto;
import ru.azor.api.common.StringResponseRequestDto;
import ru.azor.api.enums.AccountStatus;
import ru.azor.api.exceptions.ClientException;
import ru.azor.api.exceptions.ValidationException;
import ru.azor.auth.converters.UserConverter;
import ru.azor.auth.entities.Role;
import ru.azor.auth.entities.User;
import ru.azor.auth.repositories.UserRepository;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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
        if (username == null) {
            log.error("Find by username: username = null");
            return Optional.empty();
        }
        Optional<User> optionalUser = userRepository.findByUsername(username);
        log.info("Find by id: username = " + username);
        return optionalUser;
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

    public void save(User user) {
        userRepository.save(user);
    }

    public StringResponseRequestDto preSave(UserDto userDto, BindingResult bindingResult) {
        int code;
        List<ObjectError> errors = bindingResult.getAllErrors();
        if (bindingResult.hasErrors()) {
            log.error("Ошибка ввода данных при регистрации");
            throw new ValidationException("Ошибка ввода данных при регистрации", errors, HttpStatus.BAD_REQUEST);
        } else if (isUsernamePresent(userDto.getUsername())) {
            log.error("Пользователь с таким именем уже существует");
            throw new ClientException("Пользователь с таким именем уже существует", HttpStatus.CONFLICT);
        } else if (isEmailPresent(userDto.getEmail())) {
            log.error("Пользователь с таким адресом электронной почты уже существует");
            throw new ClientException("Пользователь с таким адресом электронной почты уже существует", HttpStatus.CONFLICT);
        } else {
            save(userConverter.dtoToEntity(userDto));
            code = mailService.sendMail(userDto.getEmail());
            log.info("Код подтверждения выслан на почту " + userDto.getEmail());
            log.info("Новый пользователь создан");
        }
        return StringResponseRequestDto.builder()
                .password(String.valueOf(code)).build();
    }

    public Boolean isUsernamePresent(String username) {
        return userRepository.countByUsername(username) > 0;
    }

    public Boolean isEmailPresent(String email) {
        return userRepository.countByEmail(email) > 0;
    }

    @Transactional
    public void activateUser(String username) {
        try {
            userRepository.changeStatusByUsername(AccountStatus.ACTIVE, username);
            userRepository.changeEnabledByUsername(true, username);
            log.info("Новый пользователь активирован");
        } catch (Exception e) {
            throw new ClientException("Пользователь " + username + " не найден.", HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    public void updateUserStatusAndRoles(ProfileDto profileDto, BindingResult bindingResult) {
//        if (profileDto.getRolesDto().contains(null) || profileDto.getRolesDto().isEmpty()
//                || profileDto.getRolesDto() == null) {
//            bindingResult.addError(new ObjectError("profileDto", "Должна быть выбрана хотя бы одна роль"));
//        }
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            throw new ValidationException("Ошибка валидации", errors, HttpStatus.BAD_REQUEST);
        }
        try {
            userRepository.changeStatusById(profileDto.getStatus(), profileDto.getId());
            userRepository.deleteRolesByUserId(BigInteger.valueOf(profileDto.getId()));
            profileDto.getRolesDto().forEach(roleDto -> userRepository.insertRoleByUserId(BigInteger.valueOf(roleDto.getId()), BigInteger.valueOf(profileDto.getId())));
            userRepository.changeUpdateAt(profileDto.getId());
        } catch (Exception e) {
            throw new ClientException("Пользователь с идентификатором " + profileDto.getId() + " не найден.", HttpStatus.NOT_FOUND);
        }
    }
}