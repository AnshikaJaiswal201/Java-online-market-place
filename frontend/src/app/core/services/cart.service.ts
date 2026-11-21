import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, CartSummary } from '../../shared/models/models';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = `${environment.apiUrl}/cart`;
  private cartSubject = new BehaviorSubject<CartSummary | null>(null);
  public cart$ = this.cartSubject.asObservable();

  private cartCountSubject = new BehaviorSubject<number>(0);
  public cartCount$ = this.cartCountSubject.asObservable();

  constructor(private http: HttpClient) {}

  loadCart(): Observable<ApiResponse<CartSummary>> {
    return this.http.get<ApiResponse<CartSummary>>(this.apiUrl).pipe(
      tap(res => {
        if (res.success && res.data) {
          this.updateCartState(res.data);
        }
      })
    );
  }

  addToCart(productId: number, quantity: number = 1): Observable<ApiResponse<CartSummary>> {
    return this.http.post<ApiResponse<CartSummary>>(`${this.apiUrl}/items`, { productId, quantity }).pipe(
      tap(res => {
        if (res.success && res.data) {
          this.updateCartState(res.data);
        }
      })
    );
  }

  updateQuantity(cartItemId: number, quantity: number): Observable<ApiResponse<CartSummary>> {
    return this.http.put<ApiResponse<CartSummary>>(`${this.apiUrl}/items/${cartItemId}?quantity=${quantity}`, {}).pipe(
      tap(res => {
        if (res.success && res.data) {
          this.updateCartState(res.data);
        }
      })
    );
  }

  removeFromCart(cartItemId: number): Observable<ApiResponse<CartSummary>> {
    return this.http.delete<ApiResponse<CartSummary>>(`${this.apiUrl}/items/${cartItemId}`).pipe(
      tap(res => {
        if (res.success && res.data) {
          this.updateCartState(res.data);
        }
      })
    );
  }

  clearCart(): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(this.apiUrl).pipe(
      tap(() => {
        this.cartSubject.next(null);
        this.cartCountSubject.next(0);
      })
    );
  }

  private updateCartState(summary: CartSummary): void {
    this.cartSubject.next(summary);
    this.cartCountSubject.next(summary.totalItemCount);
  }

  resetLocalState(): void {
    this.cartSubject.next(null);
    this.cartCountSubject.next(0);
  }
}
