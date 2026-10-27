import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { provideRouter } from '@angular/router';

import { authGuard } from './auth-guard';
import { AuthService } from '../services/auth.service';

describe('authGuard', () => {
  let authService: AuthService;
  let router: Router;

  const dummyRoute = {} as ActivatedRouteSnapshot;
  const dummyState = {} as RouterStateSnapshot;

  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => authGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([])
      ]
    });

    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
  });

  it('should allow navigation when user is logged in', () => {
    authService.isLoggedIn.set(true);

    const result = executeGuard(dummyRoute, dummyState);

    expect(result).toBe(true);
  });

  it('should redirect to /login when user is not logged in', () => {
    authService.isLoggedIn.set(false);

    const result = executeGuard(dummyRoute, dummyState);
    const expectedUrlTree = router.createUrlTree(['/login']);

    expect(result).toEqual(expectedUrlTree);
  });
});
