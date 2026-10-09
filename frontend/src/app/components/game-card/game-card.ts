import { Component, input } from '@angular/core';
import { UserGame } from '../../services/user-game.service';

@Component({
  selector: 'app-game-card',
  imports: [],
  templateUrl: './game-card.html',
  styleUrl: './game-card.css'
})
export class GameCard {

  game = input.required<UserGame>();
}
