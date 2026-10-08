import { Service, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserGame {
  gameId: number;
  gameTitle: string;
  gameCoverUrl: string;
  status: string;
  rating: number | null;
  notes: string | null;
}

@Service()
export class UserGameService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/user-games';

  getMyGames(): Observable<UserGame[]> {
    return this.http.get<UserGame[]>(`${this.apiUrl}/me`);
  }
}
