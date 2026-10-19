import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Login } from './pages/login/login';
import { Catalog } from './pages/catalog/catalog';
import { GameDetail } from './pages/game-detail/game-detail';
import { authGuard } from './guards/auth-guard';
import { adminGuard } from './guards/admin-guard';

export const routes: Routes = [
  { path: '', component: Catalog },
  { path: 'catalog', component: Catalog },
  { path: 'login', component: Login },
  { path: 'home', component: Home, canActivate: [authGuard] },
  { path: 'games/:id', component: GameDetail },
  {
    path: 'admin',
    canActivate: [adminGuard],
    component: Catalog
  }
];
