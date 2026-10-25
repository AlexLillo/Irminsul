import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { catchError, map, of, switchMap, tap } from 'rxjs';
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
  imports: [RouterLink, FormsModule],
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
    ? { label: '← Back to my library', route: '/home' }
    : { label: '← Back to catalog', route: '/catalog' };

  isUpdating = signal(false);
  selectedRating = signal<number | null>(null);
  selectedNotes = signal('');
  isDeleting = signal(false);

  game = signal<Game | null>(null);

  protected readonly placeholderUrl = 'images/cover-placeholder.svg';
  private readonly failedCoverUrl = signal<string | null>(null);

  coverSrc = computed(() => {
    const cover = this.game()?.coverUrl;

    return cover && cover !== this.failedCoverUrl()
      ? cover
      : this.placeholderUrl;
  });

  onCoverError(): void {
    this.failedCoverUrl.set(this.game()?.coverUrl ?? null);
  }

  userGame = signal<UserGame | null>(null);
  errorMessage = signal('');
  libraryError = signal('');
  isAdding = signal(false);
  selectedStatus = signal<GameStatus>('BACKLOG');

  isLoggedIn = this.authService.isLoggedIn;

  constructor() {
    this.route.paramMap.pipe(
      tap(() => this.resetGameState()),
      switchMap(params => {
        const id = Number(params.get('id'));

        if (!Number.isInteger(id) || id <= 0) {
          return of({
            game: null,
            userGame: null,
            gameError: true
          });
        }

        return this.gameService.getGameById(id).pipe(
          switchMap(game =>
            this.loadUserGame(id).pipe(
              map(userGame => ({
                game,
                userGame,
                gameError: false
              }))
            )
          ),
          catchError(() =>
            of({
              game: null,
              userGame: null,
              gameError: true
            })
          )
        );
      })
    ).subscribe(result => {
      this.game.set(result.game);
      this.userGame.set(result.userGame);

      if (result.gameError) {
        this.errorMessage.set(
          'Unable to find the game.'
        );
        return;
      }

      this.errorMessage.set('');

      if (result.userGame) {
        this.selectedStatus.set(result.userGame.status);
        this.selectedRating.set(result.userGame.rating);
        this.selectedNotes.set(result.userGame.notes ?? '');
      }
    });
  }

  private resetGameState(): void {
    this.game.set(null);
    this.userGame.set(null);
    this.errorMessage.set('');
    this.libraryError.set('');
    this.selectedStatus.set('BACKLOG');
    this.selectedRating.set(null);
    this.selectedNotes.set('');
  }

  private loadUserGame(gameId: number) {
    if (!this.isLoggedIn()) {
      return of(null);
    }

    return this.userGameService.getGameEntry(gameId).pipe(
      catchError(error => {
        if (error.status !== 404) {
          this.libraryError.set(
            'Unable to check whether this game is in your library.'
          );
        }

        return of(null);
      })
    );
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
          'Unable to add the game to your library. Please try again.'
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
          'Unable to update the game status. Please try again.'
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
      `Do you want to remove "${currentGame.title}" from your library?`
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
          'Unable to remove the game from your library. Please try again.'
        );
        this.isDeleting.set(false);
      }
    });
  }
}
