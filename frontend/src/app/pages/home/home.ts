import { Component, inject, signal } from '@angular/core';
import { UserGame, UserGameService } from '../../services/user-game.service';
import { GameCard } from '../../components/game-card/game-card';

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
}
