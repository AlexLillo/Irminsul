import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Platform } from '../models/game';

export interface PlatformRequest {
  name: string;
}

@Injectable({
  providedIn: 'root'
})
export class PlatformService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/platforms';

  getPlatforms(): Observable<Platform[]> {
    return this.http.get<Platform[]>(this.apiUrl);
  }

  createPlatform(platform: PlatformRequest): Observable<Platform> {
    return this.http.post<Platform>(this.apiUrl, platform);
  }

  updatePlatform(
    id: number,
    platform: PlatformRequest
  ): Observable<Platform> {
    return this.http.put<Platform>(`${this.apiUrl}/${id}`, platform);
  }

  deletePlatform(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
