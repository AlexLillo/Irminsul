import { Component, computed, inject, signal } from '@angular/core';
import { Game } from '../../models/game';
import { GameService } from '../../services/game.service';
import { GameCard } from '../../components/game-card/game-card';

type SortOption =
  | 'title-asc'
  | 'title-desc'
  | 'release-newest'
  | 'release-oldest';

@Component({
  selector: 'app-catalog',
  imports: [GameCard],
  templateUrl: './catalog.html',
  styleUrl: './catalog.css'
})
export class Catalog {
  private gameService = inject(GameService);

  games = signal<Game[]>([]);
  errorMessage = signal('');
  isLoading = signal(true);

  searchTerm = signal('');
  selectedGenre = signal('');
  selectedPlatform = signal('');
  sortOption = signal<SortOption>('title-asc');

  genres = computed(() =>
    [...new Set(this.games().map(game => game.genre))]
      .sort((a, b) => a.localeCompare(b))
  );

  platforms = computed(() => {
    const platforms = this.games()
      .flatMap(game => game.platforms)
      .map(platform => platform.name);

    return [...new Set(platforms)]
      .sort((a, b) => a.localeCompare(b));
  });

  filteredGames = computed(() => {
    const search = this.searchTerm().trim().toLocaleLowerCase();
    const genre = this.selectedGenre();
    const platform = this.selectedPlatform();
    const sort = this.sortOption();

    const result = this.games().filter(game => {
      const matchesTitle = game.title
        .toLocaleLowerCase()
        .includes(search);

      const matchesGenre =
        !genre || game.genre === genre;

      const matchesPlatform =
        !platform ||
        game.platforms.some(item => item.name === platform);

      return matchesTitle && matchesGenre && matchesPlatform;
    });

    return result.sort((a, b) => {
      switch (sort) {
        case 'title-desc':
          return b.title.localeCompare(a.title);

        case 'release-newest':
          return b.releaseDate.localeCompare(a.releaseDate);

        case 'release-oldest':
          return a.releaseDate.localeCompare(b.releaseDate);

        case 'title-asc':
        default:
          return a.title.localeCompare(b.title);
      }
    });
  });

  constructor() {
    this.gameService.getGames().subscribe({
      next: games => {
        this.games.set(games);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set(
          'No se ha podido cargar el catálogo. Inténtalo de nuevo más tarde.'
        );
        this.isLoading.set(false);
      }
    });
  }

  resetFilters(): void {
    this.searchTerm.set('');
    this.selectedGenre.set('');
    this.selectedPlatform.set('');
    this.sortOption.set('title-asc');
  }
}


