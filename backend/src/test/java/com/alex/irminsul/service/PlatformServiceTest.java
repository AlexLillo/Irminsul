package com.alex.irminsul.service;

import com.alex.irminsul.dto.PlatformRequestDTO;
import com.alex.irminsul.dto.PlatformResponseDTO;
import com.alex.irminsul.entity.Platform;
import com.alex.irminsul.exception.DuplicateResourceException;
import com.alex.irminsul.exception.ResourceInUseException;
import com.alex.irminsul.repository.GameRepository;
import com.alex.irminsul.repository.PlatformRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlatformServiceTest {

    @Mock
    private PlatformRepository platformRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private PlatformService platformService;

    @Test
    @DisplayName("Should request platforms sorted by name ascending and case-insensitive")
    void findAll_ShouldReturnAllPlatformsSorted() {
        Platform platform1 = new Platform();
        platform1.setId(1L);
        platform1.setName("Nintendo Switch");

        Platform platform2 = new Platform();
        platform2.setId(2L);
        platform2.setName("PlayStation 5");

        when(platformRepository.findAll(any(Sort.class))).thenReturn(List.of(platform1, platform2));

        List<PlatformResponseDTO> result = platformService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Nintendo Switch");
        assertThat(result.get(1).getName()).isEqualTo("PlayStation 5");

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        verify(platformRepository).findAll(sortCaptor.capture());

        Sort capturedSort = sortCaptor.getValue();
        Sort.Order order = capturedSort.getOrderFor("name");

        assertThat(order).isNotNull();
        assertThat(order.isAscending()).isTrue();
        assertThat(order.isIgnoreCase()).isTrue();
    }

    @Test
    @DisplayName("Should return platform when id exists")
    void findById_ShouldReturnPlatform_WhenExists() {
        Platform platform = new Platform();
        platform.setId(1L);
        platform.setName("PC");

        when(platformRepository.findById(1L)).thenReturn(Optional.of(platform));

        Optional<PlatformResponseDTO> result = platformService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("PC");
    }

    @Test
    @DisplayName("Should return empty optional when platform id does not exist")
    void findById_ShouldReturnEmpty_WhenDoesNotExist() {
        when(platformRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<PlatformResponseDTO> result = platformService.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should save platform when name is unique")
    void save_ShouldSavePlatform_WhenNameIsUnique() {
        PlatformRequestDTO request = new PlatformRequestDTO("Xbox Series X");

        Platform savedPlatform = new Platform();
        savedPlatform.setId(3L);
        savedPlatform.setName("Xbox Series X");

        when(platformRepository.existsByName("Xbox Series X")).thenReturn(false);
        when(platformRepository.save(any(Platform.class))).thenReturn(savedPlatform);

        PlatformResponseDTO response = platformService.save(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(3L);
        assertThat(response.getName()).isEqualTo("Xbox Series X");
        verify(platformRepository).save(any(Platform.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when platform name already exists")
    void save_ShouldThrowException_WhenNameAlreadyExists() {
        PlatformRequestDTO request = new PlatformRequestDTO("PC");

        when(platformRepository.existsByName("PC")).thenReturn(true);

        assertThatThrownBy(() -> platformService.save(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Platform already exists: PC");

        verify(platformRepository, never()).save(any(Platform.class));
    }

    @Test
    @DisplayName("Should update platform when data is valid")
    void update_ShouldUpdatePlatform_WhenValid() {
        PlatformRequestDTO request = new PlatformRequestDTO("PlayStation 5 Pro");

        Platform existingPlatform = new Platform();
        existingPlatform.setId(2L);
        existingPlatform.setName("PlayStation 5");

        when(platformRepository.existsByNameAndIdNot("PlayStation 5 Pro", 2L)).thenReturn(false);
        when(platformRepository.findById(2L)).thenReturn(Optional.of(existingPlatform));
        when(platformRepository.save(any(Platform.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<PlatformResponseDTO> result = platformService.update(2L, request);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("PlayStation 5 Pro");
        verify(platformRepository).save(existingPlatform);
    }

    @Test
    @DisplayName("Should return false when attempting to delete a non-existing platform")
    void deleteById_ShouldReturnFalse_WhenPlatformDoesNotExist() {
        when(platformRepository.existsById(99L)).thenReturn(false);

        boolean deleted = platformService.deleteById(99L);

        assertThat(deleted).isFalse();
        verify(platformRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should throw ResourceInUseException when platform is associated with games")
    void deleteById_ShouldThrowException_WhenPlatformIsUsedByGames() {
        when(platformRepository.existsById(1L)).thenReturn(true);
        when(gameRepository.existsByPlatformsId(1L)).thenReturn(true);

        assertThatThrownBy(() -> platformService.deleteById(1L))
                .isInstanceOf(ResourceInUseException.class)
                .hasMessageContaining("Platform is used by one or more games");

        verify(platformRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should delete platform when it exists and has no associated games")
    void deleteById_ShouldDeletePlatform_WhenValid() {
        when(platformRepository.existsById(1L)).thenReturn(true);
        when(gameRepository.existsByPlatformsId(1L)).thenReturn(false);

        boolean deleted = platformService.deleteById(1L);

        assertThat(deleted).isTrue();
        verify(platformRepository).deleteById(1L);
    }
}