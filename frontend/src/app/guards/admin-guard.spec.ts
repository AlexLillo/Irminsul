import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { provideRouter } from '@angular/router';

import { adminGuard } from './admin-guard';
import { AuthService } from '../services/auth.service';

describe('adminGuard', () => {
  let authService: AuthService;
  let router: Router;

  const dummyRoute = {} as ActivatedRouteSnapshot;
  const dummyState = {} as RouterStateSnapshot;

  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => adminGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([])
      ]
    });

    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
  });

  it('should redirect to /login with returnUrl query param when user is not authenticated', () => {
    authService.isLoggedIn.set(false);

    const result = executeGuard(dummyRoute, dummyState);
    const expectedUrlTree = router.createUrlTree(['/login'], {
      queryParams: { returnUrl: '/admin' }
    });

    expect(result).toEqual(expectedUrlTree);
  });

  it('should redirect to /catalog when user is authenticated but not an admin', () => {
    authService.isLoggedIn.set(true);
    authService.role.set('USER');

    const result = executeGuard(dummyRoute, dummyState);
    const expectedUrlTree = router.createUrlTree(['/catalog']);

    expect(result).toEqual(expectedUrlTree);
  });

  it('should allow navigation when user is an authenticated admin', () => {
    authService.isLoggedIn.set(true);
    authService.role.set('ADMIN');

    const result = executeGuard(dummyRoute, dummyState);

    expect(result).toBe(true);
  });
});
