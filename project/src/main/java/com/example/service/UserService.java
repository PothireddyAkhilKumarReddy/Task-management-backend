package com.example.service;

import com.example.entity.User;
import com.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // SIGNUP
    public String registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return "Username already exists!";
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Email already exists!";
        }
        
        // Hash the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default role if not provided
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        
        userRepository.save(user);
        return "User registered successfully!";
    }

    // LOGIN
    public String loginUser(String username, String password) {
        System.out.println("Checking DB for user: " + username);
        return userRepository.findByUsername(username)
                .map(user -> {
                    System.out.println("User found in DB. Checking password...");
                    if (user.getPassword().equals(password)) {
                        return "Login successful!";
                    } else {
                        System.out.println("Password mismatch!");
                        return "Invalid username or password";
                    }
                })
                .orElseGet(() -> {
                    System.out.println("User NOT found in DB.");
                    return "Invalid username or password";
                });
    }
}
