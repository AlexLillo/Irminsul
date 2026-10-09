import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Game } from '../../models/game';
import { GameService } from '../../services/game.service';

@Component({
  selector: 'app-catalog',
  imports: [RouterLink],
  templateUrl: './catalog.html',
  styleUrl: './catalog.css'
})
export class Catalog {
  private gameService = inject(GameService);
  games = signal<Game[]>([]);

  constructor() {
    this.gameService.getGames().subscribe(games => this.games.set(games));
  }
}
