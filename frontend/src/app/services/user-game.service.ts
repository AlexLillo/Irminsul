import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type GameStatus = 'BACKLOG' | 'PLAYING' | 'COMPLETED' | 'DROPPED';

export interface UserGame {
  gameId: number;
  gameTitle: string;
  gameCoverUrl: string | null;
  status: GameStatus;
  rating: number | null;
  notes: string | null;
}

export interface UserGameRequest {
  gameId: number;
  status: GameStatus;
  rating: number | null;
  notes: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class UserGameService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/user-games';

  getMyGames(): Observable<UserGame[]> {
    return this.http.get<UserGame[]>(`${this.apiUrl}/me`);
  }

  getGameEntry(gameId: number): Observable<UserGame> {
    return this.http.get<UserGame>(`${this.apiUrl}/${gameId}`);
  }

  addGame(request: UserGameRequest): Observable<UserGame> {
    return this.http.post<UserGame>(this.apiUrl, request);
  }

  updateGame(gameId: number, request: UserGameRequest): Observable<UserGame> {
    return this.http.put<UserGame>(`${this.apiUrl}/${gameId}`, request);
  }

  deleteGame(gameId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${gameId}`);
  }

}

