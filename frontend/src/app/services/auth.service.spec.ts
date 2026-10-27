import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { AuthService, AuthenticatedUser } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const API_URL = 'http://localhost:8080/api';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should initialize with logged-out state', () => {
    expect(service.isLoggedIn()).toBe(false);
    expect(service.role()).toBeNull();
    expect(service.currentUser()).toBeNull();
  });

  it('should execute login handshake, refresh CSRF and update signals on success', () => {
    const mockUser: AuthenticatedUser = {
      id: 1,
      username: 'alex',
      role: 'ADMIN'
    };

    let resultUser: AuthenticatedUser | undefined;

    service.login('alex', 'password123').subscribe(user => {
      resultUser = user;
    });

    const csrfReq1 = httpMock.expectOne(`${API_URL}/auth/csrf`);
    expect(csrfReq1.request.method).toBe('GET');
    csrfReq1.flush({ token: 'csrf-token-1', headerName: 'X-XSRF-TOKEN' });

    const loginReq = httpMock.expectOne(`${API_URL}/auth/login`);
    expect(loginReq.request.method).toBe('POST');
    expect(loginReq.request.headers.get('X-XSRF-TOKEN')).toBe('csrf-token-1');
    expect(loginReq.request.body).toEqual({ username: 'alex', password: 'password123' });
    loginReq.flush(mockUser);

    const csrfReq2 = httpMock.expectOne(`${API_URL}/auth/csrf`);
    expect(csrfReq2.request.method).toBe('GET');
    csrfReq2.flush({ token: 'csrf-token-2', headerName: 'X-XSRF-TOKEN' });

    expect(resultUser).toEqual(mockUser);
    expect(service.isLoggedIn()).toBe(true);
    expect(service.role()).toBe('ADMIN');
    expect(service.currentUser()).toEqual(mockUser);
    expect(service.isAdmin()).toBe(true);
  });

  it('should clear signals and fail when login request returns an error', () => {
    let hasError = false;
    let nextCalled = false;

    service.login('alex', 'wrong-password').subscribe({
      next: () => {
        nextCalled = true;
      },
      error: () => {
        hasError = true;
      }
    });

    const csrfReq = httpMock.expectOne(`${API_URL}/auth/csrf`);
    csrfReq.flush({ token: 'csrf-token', headerName: 'X-XSRF-TOKEN' });

    const loginReq = httpMock.expectOne(`${API_URL}/auth/login`);
    loginReq.flush({ message: 'Invalid credentials' }, { status: 401, statusText: 'Unauthorized' });

    expect(nextCalled).toBe(false);
    expect(hasError).toBe(true);
    expect(service.isLoggedIn()).toBe(false);
    expect(service.role()).toBeNull();
    expect(service.currentUser()).toBeNull();
  });

  it('should restore session and set signals when session is valid', () => {
    const mockUser: AuthenticatedUser = {
      id: 2,
      username: 'link',
      role: 'USER'
    };

    service.restoreSession().subscribe();

    const csrfReq = httpMock.expectOne(`${API_URL}/auth/csrf`);
    csrfReq.flush({ token: 'csrf-token', headerName: 'X-XSRF-TOKEN' });

    const meReq = httpMock.expectOne(`${API_URL}/users/me`);
    expect(meReq.request.method).toBe('GET');
    meReq.flush(mockUser);

    expect(service.isLoggedIn()).toBe(true);
    expect(service.role()).toBe('USER');
    expect(service.currentUser()).toEqual(mockUser);
    expect(service.isAdmin()).toBe(false);
  });

  it('should handle restoreSession failure gracefully leaving user logged out', () => {
    service.restoreSession().subscribe();

    const csrfReq = httpMock.expectOne(`${API_URL}/auth/csrf`);
    csrfReq.flush({ token: 'csrf-token', headerName: 'X-XSRF-TOKEN' });


    const meReq = httpMock.expectOne(`${API_URL}/users/me`);
    meReq.flush({ message: 'Not logged in' }, { status: 401, statusText: 'Unauthorized' });

    expect(service.isLoggedIn()).toBe(false);
    expect(service.role()).toBeNull();
    expect(service.currentUser()).toBeNull();
  });
});
