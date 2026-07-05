package com.erasm.security;

import com.erasm.entity.Employee;
import com.erasm.entity.User;
import com.erasm.repository.EmployeeRepository;
import com.erasm.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    public UserDetailsServiceImpl(UserRepository userRepository, EmployeeRepository employeeRepository) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user with email: " + email));

        // Role lives on Employee (Many-To-One Employee -> Role), reached via the
        // One-To-One User <-> Employee link.
        Employee employee = employeeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new UsernameNotFoundException("No employee profile for: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + employee.getRole().getName().name()))
                .disabled(!user.isEnabled())
                .build();
    }
}
