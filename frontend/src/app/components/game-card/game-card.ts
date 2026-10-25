import { Component, computed, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Platform } from '../../models/game';
import { GameStatus } from '../../services/user-game.service';

@Component({
  selector: 'app-game-card',
  imports: [RouterLink],
  templateUrl: './game-card.html',
  styleUrl: './game-card.css'
})
export class GameCard {
  gameId = input.required<number>();
  title = input.required<string>();
  coverUrl = input.required<string | null>();
  context = input.required<'catalog' | 'home'>();
  protected readonly statusLabels: Record<GameStatus, string> = {
    BACKLOG: 'Backlog',
    PLAYING: 'Playing',
    COMPLETED: 'Completed',
    DROPPED: 'Dropped'
  };
  private readonly placeholderUrl = 'images/cover-placeholder.svg';
  private readonly failedCoverUrl = signal<string | null>(null);

  protected readonly displayedCover = computed(() => {
    const cover = this.coverUrl();

    return cover && cover !== this.failedCoverUrl()
      ? cover
      : this.placeholderUrl;
  });

  protected onCoverError(): void {
    this.failedCoverUrl.set(this.coverUrl());
  }

  genre = input<string>();
  platforms = input<Platform[]>([]);

  averageRating = input<number | null>(null);
  ratingCount = input<number>(0);

  status = input<GameStatus>();
  rating = input<number | null>();
  notes = input<string | null>();
}
