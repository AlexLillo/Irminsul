package com.alex.irminsul.service;

import com.alex.irminsul.entity.Role;
import com.alex.irminsul.entity.User;
import com.alex.irminsul.exception.DuplicateResourceException;
import com.alex.irminsul.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateResourceException(
                    "Username already exists: " + user.getUsername());
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException(
                    "Email already exists: " + user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);

        return userRepository.save(user);
    }
}