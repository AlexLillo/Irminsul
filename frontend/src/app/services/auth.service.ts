import { Service } from '@angular/core';

@Service()
export class AuthService {

  private username: string | null = null;
  private password: string | null = null;

  login(username: string, password: string): void {
    this.username = username;
    this.password = password;
  }

  logout(): void {
    this.username = null;
    this.password = null;
  }

  isAuthenticated(): boolean {
    return this.username !== null && this.password !== null;
  }

  getAuthorizationHeader(): string | null {
    if (!this.isAuthenticated()) {
      return null;
    }

    const credentials = `${this.username}:${this.password}`;
    return `Basic ${btoa(credentials)}`;
  }
}
