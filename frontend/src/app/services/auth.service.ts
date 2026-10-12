import { Service, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Service()
export class AuthService {

  private username: string | null = null;
  private password: string | null = null;
  private http = inject(HttpClient);
  isLoggedIn = signal(false);

  login(username: string, password: string): void {
    this.username = username;
    this.password = password;
    this.isLoggedIn.set(true);
  }

  logout(): void {
    this.username = null;
    this.password = null;
    this.isLoggedIn.set(false);
  }

  isAuthenticated(): boolean {
    return this.isLoggedIn();
  }

  getAuthorizationHeader(): string | null {
    if (!this.isAuthenticated()) {
      return null;
    }

    const credentials = `${this.username}:${this.password}`;
    return `Basic ${btoa(credentials)}`;
  }

  validateCredentials(): Observable<void> {
    return this.http.get<void>('http://localhost:8080/api/user-games/me');
  }
}
