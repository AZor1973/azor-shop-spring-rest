package ru.azor.auth.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.azor.api.auth.UserDto;
import ru.azor.api.exceptions.ResourceNotFoundException;
import ru.azor.auth.entities.Role;
import ru.azor.auth.entities.User;
import ru.azor.auth.repositories.RoleRepository;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserConverter {
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder encoder;

    public User dtoToEntity(UserDto userDto) {
        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("ROLE_USER не найдена"));
        return User.builder()
                .username(userDto.getUsername())
                .firstname(userDto.getFirstname())
                .lastname(userDto.getLastname())
                .email(userDto.getEmail())
                .phone(userDto.getPhone())
                .password(encoder.encode(userDto.getPassword()))
                .status(User.AccountStatus.NOT_ACTIVE)
                .roles(new HashSet<>(Set.of(role)))
                .build();
    }

    public UserDto entityToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }
}
