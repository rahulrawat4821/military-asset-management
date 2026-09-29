package com.mams.backend.controller;

import com.mams.backend.dto.LoginRequest;
import com.mams.backend.dto.LoginResponse;
import com.mams.backend.model.User;
import com.mams.backend.repository.UserRepository;
import com.mams.backend.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid username or password");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        String token = jwtService.generateToken(user);

        LoginResponse response = new LoginResponse(
                token,
                user.getUsername(),
                user.getRole().name(),
                user.getBase() != null ? user.getBase().getId() : null,
                user.getBase() != null ? user.getBase().getName() : null
        );

        return ResponseEntity.ok(response);
    }
}
