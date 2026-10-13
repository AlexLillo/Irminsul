import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Game } from '../../models/game';
import { GameService } from '../../services/game.service';
import { AuthService } from '../../services/auth.service';
import {
  GameStatus,
  UserGame,
  UserGameService
} from '../../services/user-game.service';

@Component({
  selector: 'app-game-detail',
  imports: [RouterLink],
  templateUrl: './game-detail.html',
  styleUrl: './game-detail.css'
})
export class GameDetail {
  private route = inject(ActivatedRoute);
  private gameService = inject(GameService);
  private authService = inject(AuthService);
  private userGameService = inject(UserGameService);

  private source = this.route.snapshot.queryParamMap.get('from');

  backLink = this.source === 'home'
    ? { label: '← Volver a mi biblioteca', route: '/home' }
    : { label: '← Volver al catálogo', route: '/catalog' };

  isUpdating = signal(false);
  selectedRating = signal<number | null>(null);
  selectedNotes = signal('');
  isDeleting = signal(false);

  game = signal<Game | null>(null);
  userGame = signal<UserGame | null>(null);
  errorMessage = signal('');
  libraryError = signal('');
  isAdding = signal(false);
  selectedStatus = signal<GameStatus>('BACKLOG');

  isLoggedIn = this.authService.isLoggedIn;

  constructor() {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.gameService.getGameById(id).subscribe({
      next: game => {
        this.game.set(game);
        this.loadUserGame(id);
      },
      error: () => {
        this.errorMessage.set('No se ha podido encontrar el juego.');
      }
    });
  }

  private loadUserGame(gameId: number): void {
    if (!this.isLoggedIn()) {
      return;
    }

    this.userGameService.getGameEntry(gameId).subscribe({
      next: userGame => {
        this.userGame.set(userGame);
        this.selectedStatus.set(userGame.status);
        this.selectedRating.set(userGame.rating);
        this.selectedNotes.set(userGame.notes ?? '');
      },
      error: error => {
        if (error.status !== 404) {
          this.libraryError.set(
            'No se ha podido comprobar si el juego está en tu biblioteca.'
          );
        }
      }
    });
  }

  onStatusChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value as GameStatus;
    this.selectedStatus.set(value);
  }

  onRatingChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.selectedRating.set(value === '' ? null : Number(value));
  }

  onNotesChange(event: Event): void {
    const value = (event.target as HTMLTextAreaElement).value;
    this.selectedNotes.set(value);
  }

  addToLibrary(): void {
    const currentGame = this.game();

    if (!currentGame || this.isAdding()) {
      return;
    }

    this.isAdding.set(true);
    this.libraryError.set('');

    this.userGameService.addGame({
      gameId: currentGame.id,
      status: this.selectedStatus(),
      rating: null,
      notes: null
    }).subscribe({
      next: userGame => {
        this.userGame.set(userGame);
        this.isAdding.set(false);
      },
      error: () => {
        this.libraryError.set(
          'No se ha podido añadir el juego a tu biblioteca. Inténtalo de nuevo.'
        );
        this.isAdding.set(false);
      }
    });
  }

  updateGameStatus(): void {
    const currentGame = this.game();

    if (!currentGame || !this.userGame() || this.isUpdating()) {
      return;
    }

    this.isUpdating.set(true);
    this.libraryError.set('');

    this.userGameService.updateGame(currentGame.id, {
      gameId: currentGame.id,
      status: this.selectedStatus(),
      rating: this.selectedRating(),
      notes: this.selectedNotes().trim() || null
    }).subscribe({
      next: updatedGame => {
        this.userGame.set(updatedGame);
        this.selectedStatus.set(updatedGame.status);
        this.isUpdating.set(false);
      },
      error: () => {
        this.libraryError.set(
          'No se ha podido actualizar el estado del juego. Inténtalo de nuevo.'
        );
        this.isUpdating.set(false);
      }
    });
  }

  deleteFromLibrary(): void {
    const currentGame = this.game();

    if (!currentGame || !this.userGame() || this.isDeleting()) {
      return;
    }

    const confirmed = confirm(
      `¿Quieres eliminar "${currentGame.title}" de tu biblioteca?`
    );

    if (!confirmed) {
      return;
    }

    this.isDeleting.set(true);
    this.libraryError.set('');

    this.userGameService.deleteGame(currentGame.id).subscribe({
      next: () => {
        this.userGame.set(null);
        this.selectedStatus.set('BACKLOG');
        this.selectedRating.set(null);
        this.selectedNotes.set('');
        this.isDeleting.set(false);
      },
      error: () => {
        this.libraryError.set(
          'No se ha podido eliminar el juego de tu biblioteca. Inténtalo de nuevo.'
        );
        this.isDeleting.set(false);
      }
    });
  }
}
