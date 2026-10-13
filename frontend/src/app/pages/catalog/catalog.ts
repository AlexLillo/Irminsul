import { Component, inject, signal } from '@angular/core';
import { Game } from '../../models/game';
import { GameService } from '../../services/game.service';
import { GameCard } from '../../components/game-card/game-card';

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
}
