package ru.azor.auth.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.azor.api.auth.RoleDto;
import ru.azor.api.auth.UserDto;
import ru.azor.api.auth.ProfileDto;
import ru.azor.api.enums.AccountStatus;
import ru.azor.api.exceptions.ClientException;
import ru.azor.auth.entities.Role;
import ru.azor.auth.entities.User;
import ru.azor.auth.services.RoleService;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserConverter {
    private final RoleService roleService;
    private final BCryptPasswordEncoder encoder;

    public User dtoToEntity(UserDto userDto) {
        Role role = roleService.findByName("ROLE_USER")
                .orElseThrow(() -> new ClientException("ROLE_USER не найдена", HttpStatus.NOT_FOUND));
        return User.builder()
                .username(userDto.getUsername())
                .firstname(userDto.getFirstname())
                .lastname(userDto.getLastname())
                .email(userDto.getEmail())
                .phone(userDto.getPhone())
                .password(encoder.encode(userDto.getPassword()))
                .status(AccountStatus.NOT_ACTIVE)
                .roles(new HashSet<>(Set.of(role)))
                .build();
    }

    public ProfileDto userToProfileDto(User user) {
        return ProfileDto.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus())
                .rolesDto(rolesToRolesDto(user.getRoles()))
                .build();
    }

    public User profileDtoToUser(ProfileDto profileDto) {
        return User.builder()
                .username(profileDto.getUsername())
                .firstname(profileDto.getFirstname())
                .lastname(profileDto.getLastname())
                .email(profileDto.getEmail())
                .phone(profileDto.getPhone())
                .status(profileDto.getStatus())
                .roles(rolesDtoToRoles(profileDto.getRolesDto()))
                .build();
    }

    public Role roleDtoToRole(RoleDto roleDto){
        return Role.builder()
                .name(roleDto.getName())
                .build();
    }

    public RoleDto roleToRoleDto(Role role){
        return RoleDto.builder()
                .name(role.getName())
                .build();
    }

    public Set<Role> rolesDtoToRoles(Set<RoleDto> roles){
        return roles.stream().map(this::roleDtoToRole).collect(Collectors.toSet());
    }

    public Set<RoleDto> rolesToRolesDto(Set<Role> roles){
        return roles.stream().map(this::roleToRoleDto).collect(Collectors.toSet());
    }
}
