import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Game } from '../../models/game';
import { GameService } from '../../services/game.service';

@Component({
  selector: 'app-game-detail',
  imports: [RouterLink],
  templateUrl: './game-detail.html',
  styleUrl: './game-detail.css'
})
export class GameDetail {
  private route = inject(ActivatedRoute);
  private gameService = inject(GameService);

  game = signal<Game | null>(null);
  errorMessage = signal('');

  constructor() {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.gameService.getGameById(id).subscribe({
      next: game => {
        this.game.set(game);
      },
      error: () => {
        this.errorMessage.set('No se ha podido encontrar el juego.');
      }
    });
  }
}
