import { Component, computed, inject, signal } from '@angular/core';
import { Game } from '../../models/game';
import { GameService } from '../../services/game.service';
import { PlatformService } from '../../services/platform.service';

@Component({
  selector: 'app-admin',
  imports: [],
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class Admin {
  private gameService = inject(GameService);
  private platformService = inject(PlatformService);

  games = signal<Game[]>([]);
  platformCount = signal(0);

  searchTerm = signal('');

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
    this.actionMessage.set(
      'The game creation form will be available in the next step.'
    );
  }

  startEditing(game: Game): void {
    this.actionMessage.set(
      `The editing form for "${game.title}" will be available in the next step.`
    );
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
