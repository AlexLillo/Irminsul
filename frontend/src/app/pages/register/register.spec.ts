import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NgForm } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { Register } from './register';
import { AuthService } from '../../services/auth.service';

describe('Register', () => {
  let component: Register;
  let fixture: ComponentFixture<Register>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Register],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Register);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not register when the passwords do not match', () => {
    const authService = TestBed.inject(AuthService);
    const registerSpy = vi.spyOn(authService, 'register');

    component.form.username = 'newuser';
    component.form.email = 'new@example.com';
    component.form.password = 'password123';
    component.form.confirmPassword = 'different123';

    component.submitRegister({ invalid: false } as NgForm);

    expect(registerSpy).not.toHaveBeenCalled();
    expect(component.errorMessage()).toBe('Passwords do not match.');
  });
});
