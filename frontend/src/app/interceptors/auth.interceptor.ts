
import { HttpInterceptorFn } from '@angular/common/http';

const API_BASE_URL = 'http://localhost:8080/api/';
const SAFE_METHODS = ['GET', 'HEAD', 'OPTIONS', 'TRACE'];

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (!req.url.startsWith(API_BASE_URL)) {
    return next(req);
  }

  let headers = req.headers;

  if (!SAFE_METHODS.includes(req.method)) {
    const csrfToken = document.cookie
      .split('; ')
      .find(cookie => cookie.startsWith('XSRF-TOKEN='))
      ?.substring('XSRF-TOKEN='.length);

    if (csrfToken) {
      headers = headers.set(
        'X-XSRF-TOKEN',
        decodeURIComponent(csrfToken)
      );
    }
  }

  return next(req.clone({
    headers,
    withCredentials: true
  }));
};


