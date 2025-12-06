package com.example.controller;

import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.security.JwtUtil;
import com.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*") // allow Postman / frontend requests
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager; // Added field
    private final JwtUtil jwtUtil; // Added field
    private final UserRepository userRepository; // Added field

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) { // Modified parameter type
        String result = userService.registerUser(user); // Modified method call
        if ("User registered successfully!".equals(result)) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            
            // If authentication successful, generate token
            User user = userRepository.findByUsername(username).orElseThrow();
            String token = jwtUtil.generateToken(username, user.getRole());
            
            return ResponseEntity.ok(token);
            
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
    
    @GetMapping("/test")
    public String test() {
        return "Controller works!";
    }

}
