import { Component, computed, inject, signal } from '@angular/core';
import { Game } from '../../models/game';
import { GameService } from '../../services/game.service';
import { GameCard } from '../../components/game-card/game-card';

type SortOption =
  | 'title-asc'
  | 'title-desc'
  | 'release-newest'
  | 'release-oldest'
  | 'rating-highest'
  | 'rating-lowest';

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
        case 'release-oldest': {
          if (!a.releaseDate || !b.releaseDate) {
            if (!a.releaseDate && !b.releaseDate) {
              return a.title.localeCompare(b.title);
            }
            return a.releaseDate ? -1 : 1;
          }

          return sort === 'release-newest'
            ? b.releaseDate.localeCompare(a.releaseDate)
            : a.releaseDate.localeCompare(b.releaseDate);
        }

        case 'rating-highest':
        case 'rating-lowest': {
          const aHasRating =
            a.averageRating !== null && a.ratingCount > 0;
          const bHasRating =
            b.averageRating !== null && b.ratingCount > 0;

          // Games without ratings are always placed at the end.
          if (aHasRating !== bHasRating) {
            return aHasRating ? -1 : 1;
          }

          // If neither game has a rating, sort by title.
          if (!aHasRating || !bHasRating) {
            return a.title.localeCompare(b.title);
          }

          const ratingDifference =
            a.averageRating! - b.averageRating!;

          if (ratingDifference !== 0) {
            return sort === 'rating-highest'
              ? -ratingDifference
              : ratingDifference;
          }

          // Break ties by rating count, then by title.
          const countDifference = b.ratingCount - a.ratingCount;

          if (countDifference !== 0) {
            return countDifference;
          }

          return a.title.localeCompare(b.title);
        }

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
          'Unable to load the catalog. Please try again later.'
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



