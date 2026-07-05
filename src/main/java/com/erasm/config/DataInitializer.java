package com.erasm.config;

import com.erasm.entity.Role;
import com.erasm.entity.RoleName;
import com.erasm.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Runs once at application startup and makes sure the 5 fixed roles
 * (ADMIN, DELIVERY_MANAGER, RESOURCE_MANAGER, EMPLOYEE, AUDITOR) exist in the DB.
 * Without this, registering a user would fail because there's no Role row to attach.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        for (RoleName roleName : RoleName.values()) {
            roleRepository.findByName(roleName)
                    .orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build()));
        }
    }
}
