import { Component, computed, inject, signal } from '@angular/core';
import { Game } from '../../models/game';
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
  platformCount = signal(0);

  searchTerm = signal('');
  showGameForm = signal(false);
  selectedGame = signal<Game | null>(null);

  isLoading = signal(true);
  errorMessage = signal('');
  actionMessage = signal('');

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

  private loadPlatforms(): void {
    this.platformService.getPlatforms().subscribe({
      next: platforms => {
        this.platformCount.set(platforms.length);
      },
      error: () => {
        this.platformCount.set(0);
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
      error: () => {
        this.errorMessage.set(
          `Unable to delete "${game.title}". It may be referenced by other records.`
        );
      }
    });
  }
}
