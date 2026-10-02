package com.alex.irminsul.controller;

import com.alex.irminsul.entity.Platform;
import com.alex.irminsul.service.PlatformService;
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
    public List<Platform> findAll() {
        return platformService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Platform> findById(@PathVariable Long id) {
        return platformService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Platform save(@RequestBody Platform platform) {
        return platformService.save(platform);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Platform> update(@PathVariable Long id,
                                           @RequestBody Platform platform) {
        return platformService.update(id, platform)
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