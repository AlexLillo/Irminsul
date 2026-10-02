package com.alex.irminsul.service;

import com.alex.irminsul.entity.Platform;
import com.alex.irminsul.repository.PlatformRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlatformService {

    private final PlatformRepository platformRepository;

    public PlatformService(PlatformRepository platformRepository) {
        this.platformRepository = platformRepository;
    }

    public List<Platform> findAll() {
        return platformRepository.findAll();
    }

    public Optional<Platform> findById(Long id) {
        return platformRepository.findById(id);
    }

    public Platform save(Platform platform) {
        return platformRepository.save(platform);
    }

    public Optional<Platform> update(Long id, Platform platform) {
        return platformRepository.findById(id)
                .map(existingPlatform -> {
                    existingPlatform.setName(platform.getName());

                    return platformRepository.save(existingPlatform);
                });
    }

    public boolean deleteById(Long id) {
        if (!platformRepository.existsById(id)) {
            return false;
        }

        platformRepository.deleteById(id);
        return true;
    }
}