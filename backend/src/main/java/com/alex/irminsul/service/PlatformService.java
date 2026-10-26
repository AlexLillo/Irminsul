package com.alex.irminsul.service;

import com.alex.irminsul.dto.PlatformRequestDTO;
import com.alex.irminsul.dto.PlatformResponseDTO;
import com.alex.irminsul.entity.Platform;
import com.alex.irminsul.exception.DuplicateResourceException;
import com.alex.irminsul.repository.GameRepository;
import com.alex.irminsul.repository.PlatformRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PlatformService {

    private final PlatformRepository platformRepository;
    private final GameRepository gameRepository;

    public PlatformService(PlatformRepository platformRepository,
                           GameRepository gameRepository) {
        this.platformRepository = platformRepository;
        this.gameRepository = gameRepository;
    }

    private PlatformResponseDTO toResponseDTO(Platform platform) {
        return new PlatformResponseDTO(
                platform.getId(),
                platform.getName()
        );
    }

    public List<PlatformResponseDTO> findAll() {
        return platformRepository
                .findAll(Sort.by(Sort.Order.asc("name").ignoreCase()))
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Optional<PlatformResponseDTO> findById(Long id) {
        return platformRepository.findById(id)
                .map(this::toResponseDTO);
    }

    @Transactional
    public PlatformResponseDTO save(PlatformRequestDTO platformRequestDTO) {
        if (platformRepository.existsByName(platformRequestDTO.getName())) {
            throw new DuplicateResourceException(
                    "Platform already exists: " + platformRequestDTO.getName());
        }

        Platform platform = new Platform();
        platform.setName(platformRequestDTO.getName());

        Platform savedPlatform = platformRepository.save(platform);

        return toResponseDTO(savedPlatform);
    }

    @Transactional
    public Optional<PlatformResponseDTO> update(
            Long id,
            PlatformRequestDTO platformRequestDTO) {

        if (platformRepository.existsByNameAndIdNot(
                platformRequestDTO.getName(), id)) {

            throw new DuplicateResourceException(
                    "Platform already exists: "
                            + platformRequestDTO.getName());
        }

        return platformRepository.findById(id)
                .map(existingPlatform -> {
                    existingPlatform.setName(
                            platformRequestDTO.getName());

                    Platform updatedPlatform =
                            platformRepository.save(existingPlatform);

                    return toResponseDTO(updatedPlatform);
                });
    }

    @Transactional
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