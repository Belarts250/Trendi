package com.Trendi.demo.service;

import com.Trendi.demo.config.JwtUtil;
import com.Trendi.demo.dto.AuthResponse;
import com.Trendi.demo.dto.LoginRequest;
import com.Trendi.demo.dto.SignupRequest;
import com.Trendi.demo.entity.User;
import com.Trendi.demo.exception.BadRequestException;
import com.Trendi.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists" + request.getEmail());
        }


        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getId());

        return new AuthResponse(token, "Bearer", savedUser.getId(),
                savedUser.getName(), savedUser.getEmail());
    }

    public AuthResponse login(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new BadRequestException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());

        return new AuthResponse(token, "Bearer", user.getId(),
                user.getName(),
                user.getEmail());
    }
}

