import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { CartService } from '../../../core/services/cart.service';
import { User } from '../../models/models';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  currentUser$: Observable<User | null>;
  cartCount$: Observable<number>;

  constructor(
    public authService: AuthService,
    private cartService: CartService,
    private router: Router
  ) {
    this.currentUser$ = this.authService.currentUser$;
    this.cartCount$ = this.cartService.cartCount$;
  }

  ngOnInit(): void {
    if (this.authService.isLoggedIn() && this.authService.isCustomer()) {
      this.cartService.loadCart().subscribe({
        error: () => {} // Silent fail if not customer or error
      });
    }
  }

  logout(): void {
    this.authService.logout();
    this.cartService.resetLocalState();
    this.router.navigate(['/auth/login']);
  }
}
