import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';
import { CartService } from '../../../core/services/cart.service';
import { AuthService } from '../../../core/services/auth.service';
import { CartSummary, Order } from '../../../shared/models/models';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent implements OnInit {
  checkoutForm: FormGroup;
  cart: CartSummary | null = null;
  loading = false;
  submitting = false;
  errorMessage = '';
  placedOrder: Order | null = null;

  paymentMethods = [
    { id: 'UPI_GPAY', name: 'Instant UPI (Google Pay / PhonePe / Paytm)', icon: 'fa-solid fa-mobile-screen-button' },
    { id: 'CARD', name: 'Credit / Debit Card (Visa, MasterCard, RuPay)', icon: 'fa-regular fa-credit-card' },
    { id: 'NET_BANKING', name: 'Net Banking (SBI, HDFC, ICICI, Axis)', icon: 'fa-solid fa-building-columns' },
    { id: 'COD', name: 'Cash On Delivery (COD)', icon: 'fa-solid fa-hand-holding-dollar' }
  ];

  constructor(
    private fb: FormBuilder,
    private orderService: OrderService,
    private cartService: CartService,
    private authService: AuthService,
    private router: Router
  ) {
    const user = this.authService.currentUserValue();
    const defaultAddress = user?.addressLine1 ? `${user.addressLine1}, ${user.city || ''}, ${user.state || ''} - ${user.pinCode || ''}` : '';

    this.checkoutForm = this.fb.group({
      shippingAddress: [defaultAddress, [Validators.required, Validators.minLength(10)]],
      paymentMethod: ['UPI_GPAY', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.loading = true;
    this.cartService.loadCart().subscribe({
      next: (res) => {
        this.loading = false;
        if (res.success) {
          this.cart = res.data;
          if (!this.cart || this.cart.items.length === 0) {
            this.router.navigate(['/cart']);
          }
        }
      },
      error: () => {
        this.loading = false;
        this.router.navigate(['/cart']);
      }
    });
  }

  onSubmit(): void {
    if (this.checkoutForm.invalid || !this.cart) {
      return;
    }

    this.submitting = true;
    this.errorMessage = '';

    this.orderService.checkout(this.checkoutForm.value).subscribe({
      next: (res) => {
        this.submitting = false;
        if (res.success && res.data) {
          this.placedOrder = res.data;
          this.cartService.resetLocalState();
        }
      },
      error: (err) => {
        this.submitting = false;
        this.errorMessage = err.message || 'Checkout failed. An item may be out of stock.';
      }
    });
  }
}
