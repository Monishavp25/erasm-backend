package com.erasm.service;

import com.erasm.dto.*;
import com.erasm.entity.*;
import com.erasm.exception.UserNotFoundException;
import com.erasm.repository.*;
import com.erasm.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                        EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public JwtResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new UserNotFoundException("Role not found: " + request.getRole()));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // BCrypt - never store plain text
                .enabled(true)           
                .build();
        user = userRepository.save(user);

        Employee employee = Employee.builder()
                .fullName(request.getFullName())
                .user(user)
                .role(role)
                .totalAllocationPercent(0)
                .build();
        employeeRepository.save(employee);

        log.info("New user registered: {}", request.getEmail()); // OK to log email, never password
        String token = jwtUtil.generateToken(user.getEmail(), role.getName().name());
        return JwtResponse.builder().token(token).email(user.getEmail()).role(role.getName().name()).build();
    }

    public JwtResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException ex) {
            log.warn("Failed login attempt for email: {}", request.getEmail());
            throw ex;
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + request.getEmail()));

        Employee employee = employeeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new UserNotFoundException("Employee profile not found for: " + request.getEmail()));

        log.info("User logged in: {}", request.getEmail());
        String roleName = employee.getRole().getName().name();
        String token = jwtUtil.generateToken(user.getEmail(), roleName);
        return JwtResponse.builder().token(token).email(user.getEmail()).role(roleName).build();
    }
}
