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
        return new User(userDto.getId(), userDto.getUsername(), encoder.encode(userDto.getPassword()), userDto.getEmail(), new HashSet<>(Set.of(role)));
    }

    public UserDto entityToDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), null, user.getEmail());
    }
}
