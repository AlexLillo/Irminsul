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

  constructor() {
    this.userGameService.getMyGames().subscribe(games => {
      this.games.set(games);
    });
  }
}
