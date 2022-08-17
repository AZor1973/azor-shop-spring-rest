package ru.azor.auth.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.azor.auth.entities.Role;
import ru.azor.auth.repositories.RoleRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Optional<Role> findByName(String name) {
        if (name == null) {
            log.error("Find by name: name = null");
            return Optional.empty();
        }
        Optional<Role> optionalRole = roleRepository.findByName(name);
        log.info("Find by id: name = " + name);
        return optionalRole;
    }
}
