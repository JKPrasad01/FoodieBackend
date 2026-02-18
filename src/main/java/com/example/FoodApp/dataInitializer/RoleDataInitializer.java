package com.example.FoodApp.dataInitializer;

import com.example.FoodApp.entity.Role;
import com.example.FoodApp.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RoleDataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        List<String> roles = List.of(
                "ADMIN",
                "USER",
                "DELIVERY",
                "RESTAURANT"
        );

        for (String roleName : roles) {

            if (!roleRepository.existsByRole(roleName)) {

                Role role = Role.builder()
                        .role(roleName)
                        .description(roleName + " role")
                        .build();

                roleRepository.save(role);
            }
        }
    }
}