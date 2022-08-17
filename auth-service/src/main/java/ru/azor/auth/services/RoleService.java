package ru.azor.auth.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.azor.api.auth.RoleDto;
import ru.azor.api.exceptions.ClientException;
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
               .orElseThrow(() -> new ClientException("Роль с именем " + " не найдена.")));
    }
}
