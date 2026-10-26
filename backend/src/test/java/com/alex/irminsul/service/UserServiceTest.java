package com.alex.irminsul.service;

import com.alex.irminsul.dto.UserRequestDTO;
import com.alex.irminsul.dto.UserResponseDTO;
import com.alex.irminsul.entity.Role;
import com.alex.irminsul.entity.User;
import com.alex.irminsul.exception.DuplicateResourceException;
import com.alex.irminsul.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should successfully register a user when request data is valid")
    void save_ShouldRegisterUser_WhenDataIsValid() {
        UserRequestDTO request = new UserRequestDTO("link", "link@hyrule.com", "MasterSword123");

        when(userRepository.existsByUsername("link")).thenReturn(false);
        when(userRepository.existsByEmail("link@hyrule.com")).thenReturn(false);
        when(passwordEncoder.encode("MasterSword123")).thenReturn("encodedPasswordHashed");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("link");
        savedUser.setEmail("link@hyrule.com");
        savedUser.setPassword("encodedPasswordHashed");
        savedUser.setRole(Role.USER);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDTO response = userService.save(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("link");
        assertThat(response.getRole()).isEqualTo(Role.USER);

        verify(passwordEncoder).encode("MasterSword123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when username already exists")
    void save_ShouldThrowException_WhenUsernameAlreadyExists() {
        UserRequestDTO request = new UserRequestDTO("link", "link@hyrule.com", "MasterSword123");
        when(userRepository.existsByUsername("link")).thenReturn(true);

        assertThatThrownBy(() -> userService.save(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Username already exists: link");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists")
    void save_ShouldThrowException_WhenEmailAlreadyExists() {
        UserRequestDTO request = new UserRequestDTO("zelda", "link@hyrule.com", "Triforce2024");
        when(userRepository.existsByUsername("zelda")).thenReturn(false);
        when(userRepository.existsByEmail("link@hyrule.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.save(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email already exists: link@hyrule.com");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return user when username exists")
    void findByUsername_ShouldReturnUser_WhenExists() {
        User user = new User();
        user.setId(5L);
        user.setUsername("mario");
        user.setRole(Role.USER);

        when(userRepository.findByUsername("mario")).thenReturn(Optional.of(user));

        Optional<UserResponseDTO> result = userService.findByUsername("mario");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("mario");
        assertThat(result.get().getId()).isEqualTo(5L);
    }
}