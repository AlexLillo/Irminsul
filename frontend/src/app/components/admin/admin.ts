import { Component, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Game, Platform } from '../../models/game';
import { GameService } from '../../services/game.service';
import { PlatformService } from '../../services/platform.service';
import { GameForm } from '../game-form/game-form';


@Component({
  selector: 'app-admin',
  imports: [GameForm],
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class Admin {
  private gameService = inject(GameService);
  private platformService = inject(PlatformService);

  games = signal<Game[]>([]);
  platforms = signal<Platform[]>([]);
  platformCount = computed(() => this.platforms().length);

  searchTerm = signal('');
  showGameForm = signal(false);
  selectedGame = signal<Game | null>(null);

  showPlatformForm = signal(false);
  selectedPlatformId = signal<number | null>(null);
  platformName = signal('');

  isLoading = signal(true);
  isLoadingPlatforms = signal(true);
  isSavingPlatform = signal(false);

  errorMessage = signal('');
  actionMessage = signal('');
  platformErrorMessage = signal('');
  platformActionMessage = signal('');

  filteredGames = computed(() => {
    const search = this.searchTerm().trim().toLocaleLowerCase();

    return this.games().filter(game =>
      game.title.toLocaleLowerCase().includes(search)
    );
  });

  constructor() {
    this.loadGames();
    this.loadPlatforms();
  }

  loadGames(): void {
    this.isLoading.set(true);
    this.errorMessage.set('');

    this.gameService.getGames().subscribe({
      next: games => {
        this.games.set(games);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set(
          'Unable to load games. Please try again later.'
        );
        this.isLoading.set(false);
      }
    });
  }

  loadPlatforms(): void {
    this.isLoadingPlatforms.set(true);
    this.platformErrorMessage.set('');

    this.platformService.getPlatforms().subscribe({
      next: platforms => {
        this.platforms.set(platforms);
        this.isLoadingPlatforms.set(false);
      },
      error: () => {
        this.platformErrorMessage.set(
          'Unable to load platforms. Please try again later.'
        );
        this.isLoadingPlatforms.set(false);
      }
    });
  }

  startCreating(): void {
    this.selectedGame.set(null);
    this.actionMessage.set('');
    this.errorMessage.set('');
    this.showGameForm.set(true);
  }

  startEditing(game: Game): void {
    this.selectedGame.set(game);
    this.actionMessage.set('');
    this.errorMessage.set('');
    this.showGameForm.set(true);
  }

  cancelForm(): void {
    this.showGameForm.set(false);
    this.selectedGame.set(null);
  }

  onGameSaved(game: Game): void {
    const wasEditing = this.selectedGame() !== null;

    if (wasEditing) {
      this.games.update(games =>
        games.map(item => item.id === game.id ? game : item)
      );

      this.actionMessage.set(
        `"${game.title}" was updated successfully.`
      );
    } else {
      this.games.update(games => [...games, game]);

      this.actionMessage.set(
        `"${game.title}" was created successfully.`
      );
    }

    this.showGameForm.set(false);
    this.selectedGame.set(null);
    this.searchTerm.set('');
  }

  deleteGame(game: Game): void {
    const confirmed = window.confirm(
      `Are you sure you want to delete "${game.title}"? This action cannot be undone.`
    );

    if (!confirmed) {
      return;
    }

    this.actionMessage.set('');
    this.errorMessage.set('');

    this.gameService.deleteGame(game.id).subscribe({
      next: () => {
        this.games.update(games =>
          games.filter(item => item.id !== game.id)
        );

        this.actionMessage.set(
          `"${game.title}" was deleted successfully.`
        );
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage.set(
          error.status === 409
            ? (error.error?.error ?? `"${game.title}" is in users' libraries and cannot be deleted.`)
            : `Unable to delete "${game.title}". Please try again.`
        );
      }
    });
  }

  startCreatingPlatform(): void {
    this.selectedPlatformId.set(null);
    this.platformName.set('');
    this.platformActionMessage.set('');
    this.platformErrorMessage.set('');
    this.showPlatformForm.set(true);
  }

  startEditingPlatform(platform: Platform): void {
    this.selectedPlatformId.set(platform.id);
    this.platformName.set(platform.name);
    this.platformActionMessage.set('');
    this.platformErrorMessage.set('');
    this.showPlatformForm.set(true);
  }

  cancelPlatformForm(): void {
    this.showPlatformForm.set(false);
    this.selectedPlatformId.set(null);
    this.platformName.set('');
    this.isSavingPlatform.set(false);
  }

  savePlatform(): void {
    const name = this.platformName().trim();

    if (!name || this.isSavingPlatform()) {
      return;
    }

    this.platformErrorMessage.set('');
    this.platformActionMessage.set('');
    this.isSavingPlatform.set(true);

    const platformId = this.selectedPlatformId();
    const request = { name };

    const operation = platformId === null
      ? this.platformService.createPlatform(request)
      : this.platformService.updatePlatform(platformId, request);

    operation.subscribe({
      next: platform => {
        if (platformId === null) {
          this.platforms.update(platforms => [...platforms, platform]);
          this.platformActionMessage.set(
            `"${platform.name}" was created successfully.`
          );
        } else {
          this.platforms.update(platforms =>
            platforms.map(item =>
              item.id === platform.id ? platform : item
            )
          );
          this.platformActionMessage.set(
            `"${platform.name}" was updated successfully.`
          );
        }

        this.cancelPlatformForm();
      },
      error: error => {
        this.isSavingPlatform.set(false);

        if (error.status === 409) {
          this.platformErrorMessage.set(
            'A platform with this name already exists.'
          );
        } else if (error.status === 400) {
          this.platformErrorMessage.set(
            'Please enter a valid platform name (up to 50 characters).'
          );
        } else {
          this.platformErrorMessage.set(
            'Unable to save the platform. Please try again.'
          );
        }
      }
    });
  }

  deletePlatform(platform: Platform): void {
    const confirmed = window.confirm(
      `Are you sure you want to delete "${platform.name}"?`
    );

    if (!confirmed) {
      return;
    }

    this.platformErrorMessage.set('');
    this.platformActionMessage.set('');

    this.platformService.deletePlatform(platform.id).subscribe({
      next: () => {
        this.platforms.update(platforms =>
          platforms.filter(item => item.id !== platform.id)
        );

        this.platformActionMessage.set(
          `"${platform.name}" was deleted successfully.`
        );
      },
      error: () => {
        this.platformErrorMessage.set(
          `Unable to delete "${platform.name}". It may be used by one or more games.`
        );
      }
    });
  }
}
