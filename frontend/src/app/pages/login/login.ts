import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  private authService = inject(AuthService);
  private router = inject(Router);

  username = '';
  password = '';
  errorMessage = signal('');

  login(): void {
    this.errorMessage.set('');
    this.authService.login(this.username, this.password);

    this.authService.validateCredentials().subscribe({
      next: () => {
        this.router.navigate(['/home']);
      },
      error: () => {
        this.authService.logout();
        this.errorMessage.set('Usuario o contraseña incorrectos.');
      }
    });
  }
}
