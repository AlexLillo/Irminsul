import { Service, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Game } from '../models/game';

@Service()
export class GameService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/games';

  getGames(): Observable<Game[]> {
    return this.http.get<Game[]>(this.apiUrl);
  }

  getGameById(id: number): Observable<Game> {
    return this.http.get<Game>(`${this.apiUrl}/${id}`);
  }
}
