package com.tinqinacademy.authentication.persistence.initialize;

import com.tinqinacademy.authentication.persistence.entity.UserEntity;
import com.tinqinacademy.authentication.persistence.enums.Role;
import com.tinqinacademy.authentication.persistence.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class InitialAdminInitialize implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public InitialAdminInitialize(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Start AdminInitializer for initial Admin");

        if (!userRepository.existsByUsername("admin")) {
            UserEntity admin = UserEntity.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .firstName("admin")
                .lastName("admin")
                .email("admin@admin.com")
                .birthDate(LocalDate.ofEpochDay(1995-12-07))
                .phoneNo("0896356053")
                .role(Role.ADMIN)
                .build();
            userRepository.save(admin);
            log.info("Admin user created successfully.");
        } else {
            log.info("Admin user already exists. No action taken.");
        }
    }
}

