import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  const authorization = authService.getAuthorizationHeader();

  if (authorization) {
    req = req.clone({
      setHeaders: {
        Authorization: authorization
      }
    });
  }

  return next(req);
};
