import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { UserGameService } from './user-game.service';

describe('UserGameService', () => {
  let service: UserGameService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient()]
    });
    service = TestBed.inject(UserGameService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
