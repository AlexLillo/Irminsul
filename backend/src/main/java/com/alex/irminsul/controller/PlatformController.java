package com.alex.irminsul.controller;

import com.alex.irminsul.dto.PlatformRequestDTO;
import com.alex.irminsul.dto.PlatformResponseDTO;
import com.alex.irminsul.entity.Platform;
import com.alex.irminsul.service.PlatformService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platforms")
public class PlatformController {

    private final PlatformService platformService;

    public PlatformController(PlatformService platformService) {
        this.platformService = platformService;
    }

    @GetMapping
    public List<PlatformResponseDTO> findAll() {
        return platformService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlatformResponseDTO> findById(@PathVariable Long id) {
        return platformService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public PlatformResponseDTO save(
            @RequestBody @Valid PlatformRequestDTO platformRequestDTO) {

        return platformService.save(platformRequestDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlatformResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid PlatformRequestDTO platformRequestDTO) {

        return platformService.update(id, platformRequestDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (platformService.deleteById(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}