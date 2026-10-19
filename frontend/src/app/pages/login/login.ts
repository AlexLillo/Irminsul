import { Component, inject, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  username = '';
  password = '';

  errorMessage = signal('');
  isLoading = signal(false);

  submitLogin(form: NgForm): void {
    this.errorMessage.set('');

    if (form.invalid) {
      this.errorMessage.set('Please enter your username and password.');
      return;
    }

    if (this.isLoading()) {
      return;
    }

    this.login();
  }

  login(): void {
    this.isLoading.set(true);
    this.authService.login(this.username, this.password);

    this.authService.validateCredentials().subscribe({
      next: () => {
        this.isLoading.set(false);

        const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');
        const destination = returnUrl?.startsWith('/') && !returnUrl.startsWith('//')
          ? returnUrl
          : '/home';

        this.router.navigateByUrl(destination);
      },
      error: (error: HttpErrorResponse) => {
        this.authService.logout();

        if (error.status === 401 || error.status === 403) {
          this.errorMessage.set('Incorrect username or password.');
        } else if (error.status === 0) {
          this.errorMessage.set(
            'Unable to connect to the server. Please try again later.'
          );
        } else {
          this.errorMessage.set(
            'An error occurred while logging in. Please try again.'
          );
        }

        this.isLoading.set(false);
      }
    });
  }
}
