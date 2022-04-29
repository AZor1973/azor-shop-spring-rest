package ru.azor.auth.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.azor.api.auth.RoleDto;
import ru.azor.api.exceptions.ResourceNotFoundException;
import ru.azor.auth.converters.RoleConverter;
import ru.azor.auth.entities.Role;
import ru.azor.auth.repositories.RoleRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleConverter roleConverter;

    public List<Role> findAll(){
        return roleRepository.findAll();
    }

    public RoleDto findRoleByName(String name){
       return roleConverter.roleToRoleDto(roleRepository.findByName(name)
               .orElseThrow(() -> new ResourceNotFoundException("Роль с именем " + " не найдена.")));
    }
}
