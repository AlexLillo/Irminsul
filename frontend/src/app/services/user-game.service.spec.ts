import { TestBed } from '@angular/core/testing';
import { UserGame } from './user-game';

describe('UserGame', () => {
  let service: UserGame;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserGame);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
