package com.alex.irminsul.controller;

import com.alex.irminsul.dto.UserRequestDTO;
import com.alex.irminsul.dto.UserResponseDTO;
import com.alex.irminsul.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponseDTO> findAll() {
        return userService.findAll();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> findMe(
            Authentication authentication) {

        return userService.findByUsername(authentication.getName())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(
            @PathVariable Long id) {

        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public UserResponseDTO save(
            @RequestBody @Valid UserRequestDTO userRequestDTO) {

        return userService.save(userRequestDTO);
    }
}