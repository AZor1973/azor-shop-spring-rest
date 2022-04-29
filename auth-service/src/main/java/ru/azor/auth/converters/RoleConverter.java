package ru.azor.auth.converters;

import org.springframework.stereotype.Component;
import ru.azor.api.auth.RoleDto;
import ru.azor.auth.entities.Role;

@Component
public class RoleConverter {
    public RoleDto roleToRoleDto(Role role){
        return RoleDto.builder()
                .name(role.getName())
                .build();
    }
}
