import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CartService } from '../../../core/services/cart.service';
import { CartItem, CartSummary } from '../../../shared/models/models';

@Component({
  selector: 'app-cart-view',
  templateUrl: './cart-view.component.html',
  styleUrls: ['./cart-view.component.css']
})
export class CartViewComponent implements OnInit {
  cart: CartSummary | null = null;
  loading = false;
  errorMessage = '';

  constructor(
    private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.fetchCart();
  }

  fetchCart(): void {
    this.loading = true;
    this.cartService.loadCart().subscribe({
      next: (res) => {
        this.loading = false;
        if (res.success) {
          this.cart = res.data;
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.message || 'Failed to load shopping cart';
      }
    });
  }

  increaseQuantity(item: CartItem): void {
    if (item.quantity >= item.availableStock) {
      alert(`Cannot exceed available stock of ${item.availableStock} units`);
      return;
    }
    this.cartService.updateQuantity(item.cartItemId, item.quantity + 1).subscribe({
      next: (res) => {
        if (res.success) {
          this.cart = res.data;
        }
      },
      error: (err) => alert(err.message)
    });
  }

  decreaseQuantity(item: CartItem): void {
    if (item.quantity <= 1) {
      this.removeItem(item);
      return;
    }
    this.cartService.updateQuantity(item.cartItemId, item.quantity - 1).subscribe({
      next: (res) => {
        if (res.success) {
          this.cart = res.data;
        }
      },
      error: (err) => alert(err.message)
    });
  }

  removeItem(item: CartItem): void {
    if (confirm(`Remove "${item.productName}" from your cart?`)) {
      this.cartService.removeFromCart(item.cartItemId).subscribe({
        next: (res) => {
          if (res.success) {
            this.cart = res.data;
          }
        },
        error: (err) => alert(err.message)
      });
    }
  }

  clearCart(): void {
    if (confirm('Are you sure you want to empty your entire cart?')) {
      this.cartService.clearCart().subscribe({
        next: () => {
          this.cart = null;
        },
        error: (err) => alert(err.message)
      });
    }
  }

  proceedToCheckout(): void {
    this.router.navigate(['/checkout']);
  }
}
