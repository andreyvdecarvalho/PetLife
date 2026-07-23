package com.petlife.modules.auth.infrastructure.seed;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.auth.domain.entity.UserPlan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Seeder de dados de teste para ambiente de desenvolvimento.
 * Garante a existência do usuário 'teste@petlife.com' / 'Senha123@' no banco de dados.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TestDataSeeder implements CommandLineRunner {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String testEmail = "teste@petlife.com";
        
        if (!userRepository.existsByEmail(testEmail)) {
            log.info("Semeando usuário de teste padrão no banco de dados: {}", testEmail);
            
            User user = new User();
            user.setName("Tutor de Teste");
            user.setEmail(testEmail);
            user.setPasswordHash(passwordEncoder.encode("Senha123@"));
            user.setPlan(UserPlan.FREE);
            user.setEmailVerified(true);
            user.setLgpdAcceptedAt(LocalDateTime.now());
            
            userRepository.save(user);
            log.info("Usuário de teste semeado com sucesso!");
        } else {
            log.info("Usuário de teste '{}' já existe no banco de dados.", testEmail);
        }
    }
}
