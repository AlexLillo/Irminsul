import { Component, inject, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  form = {
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
  };

  errorMessage = signal('');
  isLoading = signal(false);

  submitRegister(registerForm: NgForm): void {
    this.errorMessage.set('');

    if (registerForm.invalid) {
      registerForm.control.markAllAsTouched();
      this.errorMessage.set('Please fix the highlighted fields.');
      return;
    }

    if (this.form.password !== this.form.confirmPassword) {
      this.errorMessage.set('Passwords do not match.');
      return;
    }

    if (this.isLoading()) {
      return;
    }

    this.register();
  }

  private register(): void {
    this.isLoading.set(true);

    const username = this.form.username.trim();
    const email = this.form.email.trim();
    const password = this.form.password;

    this.authService.register({ username, email, password }).subscribe({
      next: () => this.loginAfterRegister(username, password),
      error: (error: HttpErrorResponse) => {
        this.errorMessage.set(this.getErrorMessage(error));
        this.isLoading.set(false);
      }
    });
  }

  private loginAfterRegister(username: string, password: string): void {
    this.authService.login(username, password).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.form.password = '';
        this.form.confirmPassword = '';
        this.router.navigateByUrl('/home');
      },
      error: () => {
        // The account exists but the automatic login failed.
        this.isLoading.set(false);
        this.router.navigateByUrl('/login');
      }
    });
  }

  private getErrorMessage(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return 'Unable to connect to the server. Please try again later.';
    }

    if (error.status === 409) {
      return error.error?.error
        ?? 'That username or email is already in use.';
    }

    if (error.status === 400 && error.error
      && typeof error.error === 'object') {
      const messages = Object.entries(error.error as Record<string, string>)
        .map(([field, message]) => `${field}: ${message}`);

      if (messages.length > 0) {
        return messages.join('. ');
      }
    }

    return 'An error occurred while creating your account. Please try again.';
  }
}
