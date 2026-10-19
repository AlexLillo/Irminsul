
import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { App } from './app';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideHttpClient(),
        provideRouter([])
      ]
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);

    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should render the Irminsul brand', () => {
    const fixture = TestBed.createComponent(App);

    fixture.detectChanges();

    const brand = fixture.nativeElement.querySelector(
      'app-navbar .brand'
    ) as HTMLAnchorElement | null;

    expect(brand).not.toBeNull();
    expect(brand?.textContent?.trim()).toBe('Irminsul');
  });
});

