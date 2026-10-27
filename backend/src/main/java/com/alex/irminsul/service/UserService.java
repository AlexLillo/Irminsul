package com.alex.irminsul.service;

import com.alex.irminsul.dto.UserRequestDTO;
import com.alex.irminsul.dto.UserResponseDTO;
import com.alex.irminsul.entity.Role;
import com.alex.irminsul.entity.User;
import com.alex.irminsul.exception.DuplicateResourceException;
import com.alex.irminsul.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }

    public List<UserResponseDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Optional<UserResponseDTO> findById(Long id) {
        return userRepository.findById(id)
                .map(this::toResponseDTO);
    }

    public Optional<UserResponseDTO> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::toResponseDTO);
    }

    @Transactional
    public UserResponseDTO save(UserRequestDTO userRequestDTO) {
        String username = userRequestDTO.getUsername().trim();
        String normalizedEmail = userRequestDTO.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException(
                    "Username already exists: " + username);
        }

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateResourceException(
                    "Email already exists: " + normalizedEmail);
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        return toResponseDTO(savedUser);
    }
}