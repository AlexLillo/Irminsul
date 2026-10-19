import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { GameCard } from './game-card';

describe('GameCard', () => {
  let component: GameCard;
  let fixture: ComponentFixture<GameCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GameCard],
      providers: [provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(GameCard);

    fixture.componentRef.setInput('gameId', 1);
    fixture.componentRef.setInput('title', 'Test Game');
    fixture.componentRef.setInput('coverUrl', 'https://example.com/game.jpg');
    fixture.componentRef.setInput('context', 'catalog');

    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

