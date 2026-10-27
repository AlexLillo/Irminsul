import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { Catalog } from './catalog';
import { GameService } from '../../services/game.service';
import { Game } from '../../models/game';

describe('Catalog', () => {
  let component: Catalog;
  let fixture: ComponentFixture<Catalog>;

  const mockGames: Game[] = [
    {
      id: 1,
      title: 'The Legend of Zelda: Breath of the Wild',
      description: 'Open world adventure',
      releaseDate: '2017-03-03',
      coverUrl: null,
      genre: 'ADVENTURE',
      platforms: [{ id: 1, name: 'Nintendo Switch' }],
      averageRating: 9.6,
      ratingCount: 120
    },
    {
      id: 2,
      title: 'Bloodborne',
      description: 'Gothic action RPG',
      releaseDate: '2015-03-24',
      coverUrl: null,
      genre: 'RPG',
      platforms: [{ id: 2, name: 'PlayStation 4' }],
      averageRating: 9.4,
      ratingCount: 85
    },
    {
      id: 3,
      title: 'Hollow Knight',
      description: 'Challenging metroidvania',
      releaseDate: '2017-02-24',
      coverUrl: null,
      genre: 'PLATFORMER',
      platforms: [
        { id: 1, name: 'Nintendo Switch' },
        { id: 3, name: 'PC' }
      ],
      averageRating: 9.3,
      ratingCount: 60
    }
  ];

  beforeEach(async () => {
    const gameServiceMock = {
      getGames: () => of(mockGames)
    };

    await TestBed.configureTestingModule({
      imports: [Catalog],
      providers: [
        provideRouter([]),
        { provide: GameService, useValue: gameServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Catalog);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create the component and load games', () => {
    expect(component).toBeTruthy();
    expect(component.games().length).toBe(3);
    expect(component.isLoading()).toBe(false);
  });

  it('should compute unique genres and platforms sorted alphabetically', () => {
    expect(component.genres()).toEqual(['ADVENTURE', 'PLATFORMER', 'RPG']);
    expect(component.platforms()).toEqual(['Nintendo Switch', 'PC', 'PlayStation 4']);
  });

  it('should filter games by search term case-insensitively', () => {
    component.searchTerm.set('zelda');

    const filtered = component.filteredGames();
    expect(filtered.length).toBe(1);
    expect(filtered[0].title).toContain('Zelda');
  });

  it('should filter games by selected genre', () => {
    component.selectedGenre.set('RPG');

    const filtered = component.filteredGames();
    expect(filtered.length).toBe(1);
    expect(filtered[0].title).toBe('Bloodborne');
  });

  it('should filter games by platform', () => {
    component.selectedPlatform.set('PC');

    const filtered = component.filteredGames();
    expect(filtered.length).toBe(1);
    expect(filtered[0].title).toBe('Hollow Knight');
  });

  it('should sort games by rating in descending order', () => {
    component.sortOption.set('rating-highest');

    const filtered = component.filteredGames();
    expect(filtered[0].id).toBe(1);
    expect(filtered[1].id).toBe(2);
    expect(filtered[2].id).toBe(3);
  });

  it('should reset all filters when resetFilters is called', () => {
    component.searchTerm.set('zelda');
    component.selectedGenre.set('ADVENTURE');
    component.selectedPlatform.set('Nintendo Switch');
    component.sortOption.set('rating-highest');

    component.resetFilters();

    expect(component.searchTerm()).toBe('');
    expect(component.selectedGenre()).toBe('');
    expect(component.selectedPlatform()).toBe('');
    expect(component.sortOption()).toBe('title-asc');
    expect(component.filteredGames().length).toBe(3);
  });
});
