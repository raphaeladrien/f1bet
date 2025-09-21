package com.sporty.f1bet.runner;

import com.sporty.f1bet.entity.User;
import com.sporty.f1bet.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UserRunner implements CommandLineRunner {

    private final UserRepository userRepository;

    public UserRunner(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        final List<User> users = List.of(
                new User(new BigDecimal(100), User.UserRole.USER),
                new User(new BigDecimal(100), User.UserRole.USER),
                new User(new BigDecimal(100), User.UserRole.ADMIN));

        userRepository.saveAll(users);
    }
}
