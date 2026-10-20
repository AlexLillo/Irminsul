import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
  inject,
  signal
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Game, Platform } from '../../models/game';
import { GameService, GameRequest } from '../../services/game.service';
import { PlatformService } from '../../services/platform.service';

@Component({
  selector: 'app-game-form',
  imports: [FormsModule],
  templateUrl: './game-form.html',
  styleUrl: './game-form.css'
})
export class GameForm implements OnChanges {
  @Input() game: Game | null = null;

  @Output() saved = new EventEmitter<Game>();
  @Output() cancelled = new EventEmitter<void>();

  private gameService = inject(GameService);
  private platformService = inject(PlatformService);

  readonly genres = [
    'ACTION',
    'ADVENTURE',
    'FIGHTING',
    'HORROR',
    'PLATFORMER',
    'PUZZLE',
    'RACING',
    'RPG',
    'SIMULATION',
    'SPORTS',
    'STRATEGY'
  ];

  platforms: Platform[] = [];
  selectedPlatformIds: number[] = [];

  isLoadingPlatforms = signal(true);
  isSaving = signal(false);
  errorMessage = signal('');

  form = {
    title: '',
    description: '',
    releaseDate: '',
    coverUrl: '',
    genre: ''
  };

  constructor() {
    this.loadPlatforms();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['game']) {
      this.populateForm();
    }
  }

  get isEditing(): boolean {
    return this.game !== null;
  }

  private populateForm(): void {
    if (this.game) {
      this.form = {
        title: this.game.title,
        description: this.game.description ?? '',
        releaseDate: this.game.releaseDate ?? '',
        coverUrl: this.game.coverUrl ?? '',
        genre: this.game.genre
      };

      this.selectedPlatformIds = this.game.platforms.map(
        platform => platform.id
      );
    } else {
      this.form = {
        title: '',
        description: '',
        releaseDate: '',
        coverUrl: '',
        genre: ''
      };

      this.selectedPlatformIds = [];
    }

    this.errorMessage.set('');
  }

  private loadPlatforms(): void {
    this.platformService.getPlatforms().subscribe({
      next: platforms => {
        this.platforms = platforms;
        this.isLoadingPlatforms.set(false);
      },
      error: () => {
        this.errorMessage.set(
          'Unable to load platforms. Please try again.'
        );
        this.isLoadingPlatforms.set(false);
      }
    });
  }

  togglePlatform(platformId: number, selected: boolean): void {
    if (selected) {
      if (!this.selectedPlatformIds.includes(platformId)) {
        this.selectedPlatformIds = [
          ...this.selectedPlatformIds,
          platformId
        ];
      }
    } else {
      this.selectedPlatformIds = this.selectedPlatformIds.filter(
        id => id !== platformId
      );
    }
  }

  isPlatformSelected(platformId: number): boolean {
    return this.selectedPlatformIds.includes(platformId);
  }

  submit(): void {
    if (this.isSaving()) {
      return;
    }

    this.errorMessage.set('');
    this.isSaving.set(true);

    const request: GameRequest = {
      title: this.form.title.trim(),
      description: this.form.description.trim() || null,
      releaseDate: this.form.releaseDate || null,
      coverUrl: this.form.coverUrl.trim() || null,
      genre: this.form.genre,
      platformIds: [...this.selectedPlatformIds]
    };

    const operation = this.game
      ? this.gameService.updateGame(this.game.id, request)
      : this.gameService.createGame(request);

    operation.subscribe({
      next: game => {
        this.isSaving.set(false);
        this.saved.emit(game);
      },
      error: error => {
        this.isSaving.set(false);

        if (error.status === 409) {
          this.errorMessage.set(
            'A game with this title already exists.'
          );
        } else if (error.status === 400) {
          this.errorMessage.set(
            'Please check the information entered and try again.'
          );
        } else {
          this.errorMessage.set(
            'Unable to save the game. Please try again.'
          );
        }
      }
    });
  }
}
