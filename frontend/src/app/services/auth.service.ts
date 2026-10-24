import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {
  Observable,
  catchError,
  map,
  of,
  switchMap,
  tap,
  throwError
} from 'rxjs';

export type UserRole = 'USER' | 'ADMIN';

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface AuthenticatedUser {
  id: number;
  username: string;
  role: UserRole;
}

interface CsrfResponse {
  token: string;
  headerName: string;
}

const API_URL = 'http://localhost:8080/api';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);

  readonly isLoggedIn = signal(false);
  readonly role = signal<UserRole | null>(null);
  readonly currentUser = signal<AuthenticatedUser | null>(null);

  login(username: string, password: string): Observable<AuthenticatedUser> {
    return this.getCsrfToken().pipe(
      switchMap(csrf =>
        this.http.post<AuthenticatedUser>(
          `${API_URL}/auth/login`,
          { username, password },
          {
            headers: new HttpHeaders({
              [csrf.headerName]: csrf.token
            }),
            withCredentials: true
          }
        )
      ),
      switchMap(user =>
        // Refresh the CSRF token after authentication.
        this.getCsrfToken().pipe(map(() => user))
      ),
      tap(user => this.setAuthenticatedUser(user)),
      catchError(error => {
        this.clearLocalSession();
        return throwError(() => error);
      })
    );
  }

  register(request: RegisterRequest): Observable<AuthenticatedUser> {
    return this.getCsrfToken().pipe(
      switchMap(csrf =>
        this.http.post<AuthenticatedUser>(
          `${API_URL}/users`,
          request,
          {
            headers: new HttpHeaders({
              [csrf.headerName]: csrf.token
            }),
            withCredentials: true
          }
        )
      )
    );
  }

  restoreSession(): Observable<void> {
    return this.getCsrfToken().pipe(
      switchMap(() =>
        this.http.get<AuthenticatedUser>(
          `${API_URL}/users/me`,
          { withCredentials: true }
        )
      ),
      tap(user => this.setAuthenticatedUser(user)),
      map(() => undefined),
      catchError(() => {
        this.clearLocalSession();
        return of(undefined);
      })
    );
  }

  logout(): Observable<void> {
    return this.getCsrfToken().pipe(
      switchMap(csrf =>
        this.http.post<void>(
          `${API_URL}/auth/logout`,
          {},
          {
            headers: new HttpHeaders({
              [csrf.headerName]: csrf.token
            }),
            withCredentials: true
          }
        )
      ),
      tap(() => this.clearLocalSession()),
      catchError(error => {
        this.clearLocalSession();
        return throwError(() => error);
      })
    );
  }

  validateCredentials(): Observable<AuthenticatedUser> {
    return this.http.get<AuthenticatedUser>(
      `${API_URL}/users/me`,
      { withCredentials: true }
    ).pipe(
      tap(user => this.setAuthenticatedUser(user))
    );
  }

  isAuthenticated(): boolean {
    return this.isLoggedIn();
  }

  isAdmin(): boolean {
    return this.role() === 'ADMIN';
  }

  private getCsrfToken(): Observable<CsrfResponse> {
    return this.http.get<CsrfResponse>(
      `${API_URL}/auth/csrf`,
      { withCredentials: true }
    );
  }

  private setAuthenticatedUser(user: AuthenticatedUser): void {
    this.currentUser.set(user);
    this.isLoggedIn.set(true);
    this.role.set(user.role);
  }

  private clearLocalSession(): void {
    this.currentUser.set(null);
    this.isLoggedIn.set(false);
    this.role.set(null);
  }
}
