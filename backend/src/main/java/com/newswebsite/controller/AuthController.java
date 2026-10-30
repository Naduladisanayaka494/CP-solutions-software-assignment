package com.newswebsite.controller;

import com.newswebsite.dto.AuthResponse;
import com.newswebsite.dto.LoginRequest;
import com.newswebsite.dto.RegisterRequest;
import com.newswebsite.model.User;
import com.newswebsite.repository.UserRepository;
import com.newswebsite.security.JwtUtils;
import com.newswebsite.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateTokenFromUsername(userDetails.getUsername());

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(item -> item.getAuthority())
                .orElse("ROLE_READER");

        return ResponseEntity.ok(new AuthResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                role
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Username is already taken!");
            return ResponseEntity.badRequest().body(response);
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Email is already in use!");
            return ResponseEntity.badRequest().body(response);
        }

        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                "ROLE_READER"
        );

        User savedUser = userRepository.save(user);

        String jwt = jwtUtils.generateTokenFromUsername(savedUser.getUsername());

        return ResponseEntity.ok(new AuthResponse(
                jwt,
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole()
        ));
    }
}
