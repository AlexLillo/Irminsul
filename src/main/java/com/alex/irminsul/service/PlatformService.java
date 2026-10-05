package com.alex.irminsul.service;

import com.alex.irminsul.entity.Platform;
import com.alex.irminsul.exception.DuplicateResourceException;
import com.alex.irminsul.repository.GameRepository;
import com.alex.irminsul.repository.PlatformRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlatformService {

    private final PlatformRepository platformRepository;

    private final GameRepository gameRepository;

    public PlatformService(PlatformRepository platformRepository,
                           GameRepository gameRepository) {
        this.platformRepository = platformRepository;
        this.gameRepository = gameRepository;
    }

    public List<Platform> findAll() {
        return platformRepository.findAll();
    }

    public Optional<Platform> findById(Long id) {
        return platformRepository.findById(id);
    }

    public Platform save(Platform platform) {
        if (platformRepository.existsByName(platform.getName())) {
            throw new DuplicateResourceException(
                    "Platform already exists: " + platform.getName());
        }

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

        if (gameRepository.existsByPlatformsId(id)) {
            throw new DuplicateResourceException(
                    "Platform is used by one or more games");
        }

        platformRepository.deleteById(id);
        return true;
    }
}