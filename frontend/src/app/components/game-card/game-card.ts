import { Component, input } from '@angular/core';
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

  genre = input<string>();
  platforms = input<Platform[]>([]);

  averageRating = input<number | null>(null);
  ratingCount = input<number>(0);

  status = input<GameStatus>();
  rating = input<number | null>();
  notes = input<string | null>();
}
