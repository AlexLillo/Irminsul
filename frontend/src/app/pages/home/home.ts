import { Component, computed, inject, signal } from '@angular/core';
import { UserGame, UserGameService, GameStatus } from '../../services/user-game.service';
import { GameCard } from '../../components/game-card/game-card';

type SortOption =
  | 'title-asc'
  | 'title-desc'
  | 'rating-highest'
  | 'rating-lowest';

@Component({
  selector: 'app-home',
  imports: [GameCard],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home {
  private userGameService = inject(UserGameService);

  games = signal<UserGame[]>([]);
  errorMessage = signal('');
  isLoading = signal(true);

  searchTerm = signal('');
  selectedStatus = signal<GameStatus | ''>('');
  sortOption = signal<SortOption>('title-asc');

  statuses: { value: GameStatus; label: string }[] = [
    { value: 'BACKLOG', label: 'Por jugar' },
    { value: 'PLAYING', label: 'Jugando' },
    { value: 'COMPLETED', label: 'Completado' },
    { value: 'DROPPED', label: 'Abandonado' }
  ];

  filteredGames = computed(() => {
    const search = this.searchTerm().trim().toLocaleLowerCase();
    const status = this.selectedStatus();
    const sort = this.sortOption();

    const result = this.games().filter(game => {
      const matchesTitle = game.gameTitle
        .toLocaleLowerCase()
        .includes(search);

      const matchesStatus = !status || game.status === status;

      return matchesTitle && matchesStatus;
    });

    return result.sort((a, b) => {
      switch (sort) {
        case 'title-desc':
          return b.gameTitle.localeCompare(a.gameTitle);

        case 'rating-highest':
        case 'rating-lowest': {
          // Los juegos sin puntuación quedan al final en ambos órdenes.
          if (a.rating === null && b.rating !== null) return 1;
          if (a.rating !== null && b.rating === null) return -1;

          if (a.rating !== null && b.rating !== null) {
            const difference = sort === 'rating-highest'
              ? b.rating - a.rating
              : a.rating - b.rating;

            if (difference !== 0) return difference;
          }

          return a.gameTitle.localeCompare(b.gameTitle);
        }

        case 'title-asc':
        default:
          return a.gameTitle.localeCompare(b.gameTitle);
      }
    });
  });

  constructor() {
    this.userGameService.getMyGames().subscribe({
      next: games => {
        this.games.set(games);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set(
          'No se ha podido cargar tu biblioteca. Inténtalo de nuevo más tarde.'
        );
        this.isLoading.set(false);
      }
    });
  }

  resetFilters(): void {
    this.searchTerm.set('');
    this.selectedStatus.set('');
    this.sortOption.set('title-asc');
  }
}
