# ShopVerse — Frontend Source Code (Project Report Documentation)

**Technology Stack:** Angular 14, TypeScript 4.8, RxJS 7.x, Reactive Forms, HTML5, CSS3, FontAwesome 6  
**Pattern:** Modular Single Page Application (SPA), Observable State Management, Route Guards, HTTP Interceptors.

---

## 1. Application Routing & Guards (`app-routing.module.ts`)
Controls URL navigation, role-based access control, and route authentication guards.

```typescript
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { ProductListComponent } from './features/customer/catalog/product-list.component';
import { CartViewComponent } from './features/customer/cart/cart-view.component';
import { CheckoutComponent } from './features/customer/checkout/checkout.component';
import { OrderHistoryComponent } from './features/customer/orders/order-history.component';

import { SellerDashboardComponent } from './features/seller/dashboard/seller-dashboard.component';
import { SellerProductsComponent } from './features/seller/products/seller-products.component';
import { SellerInventoryComponent } from './features/seller/inventory/seller-inventory.component';
import { SellerOrdersComponent } from './features/seller/orders/seller-orders.component';

import { AuthGuard } from './core/guards/auth.guard';
import { RoleGuard } from './core/guards/role.guard';

const routes: Routes = [
  { path: '', redirectTo: 'products', pathMatch: 'full' },
  { path: 'products', component: ProductListComponent },

  // Authentication Routes
  { path: 'login', redirectTo: 'auth/login', pathMatch: 'full' },
  { path: 'register', redirectTo: 'auth/register', pathMatch: 'full' },
  { path: 'auth/login', component: LoginComponent },
  { path: 'auth/register', component: RegisterComponent },

  // Customer Protected Routes
  {
    path: 'cart',
    component: CartViewComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'ROLE_CUSTOMER' }
  },
  {
    path: 'checkout',
    component: CheckoutComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'ROLE_CUSTOMER' }
  },
  {
    path: 'orders',
    component: OrderHistoryComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'ROLE_CUSTOMER' }
  },

  // Seller Protected Routes
  {
    path: 'seller/dashboard',
    component: SellerDashboardComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'ROLE_SELLER' }
  },
  {
    path: 'seller/products',
    component: SellerProductsComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'ROLE_SELLER' }
  },
  {
    path: 'seller/inventory',
    component: SellerInventoryComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'ROLE_SELLER' }
  },
  {
    path: 'seller/orders',
    component: SellerOrdersComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'ROLE_SELLER' }
  },

  { path: '**', redirectTo: 'products' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
```

---

## 2. Shared Data Models (`models.ts`)
Standard TypeScript interfaces reflecting backend entities and DTOs.

```typescript
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface User {
  userId: number;
  email: string;
  fullName: string;
  phoneNumber?: string;
  addressLine1?: string;
  city?: string;
  state?: string;
  pinCode?: string;
  roles: string[];
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  userId: number;
  email: string;
  fullName: string;
  roles: string[];
}

export interface Category {
  categoryId: number;
  categoryName: string;
  description: string;
}

export interface Product {
  productId: number;
  name: string;
  description: string;
  price: number;
  sku: string;
  imageUrl: string;
  isActive: number;
  categoryId: number;
  categoryName: string;
  sellerId: number;
  sellerName: string;
  stockQuantity: number;
  lowStockThreshold: number;
  lowStock: boolean;
  createdAt: string;
}

export interface CartItem {
  cartItemId: number;
  productId: number;
  productName: string;
  sku: string;
  price: number;
  imageUrl: string;
  quantity: number;
  subtotal: number;
  availableStock: number;
}

export interface CartSummary {
  items: CartItem[];
  totalAmount: number;
  totalItemCount: number;
}

export interface OrderItem {
  orderItemId: number;
  productId: number;
  productName: string;
  sku: string;
  imageUrl: string;
  sellerId: number;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export interface Order {
  orderId: number;
  orderNumber: string;
  totalAmount: number;
  orderStatus: string;
  shippingAddress: string;
  paymentMethod: string;
  paymentStatus: string;
  items: OrderItem[];
  createdAt: string;
}

export interface SellerDashboardMetrics {
  totalRevenue: number;
  totalOrders: number;
  totalProducts: number;
  lowStockCount: number;
}
```

---

## 3. Authentication & State Management Service (`auth.service.ts`)
Manages JWT tokens in LocalStorage, decodes user credentials, and broadcasts current user state via RxJS `BehaviorSubject`.

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, AuthResponse, User } from '../../shared/models/models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadStoredUser();
  }

  private loadStoredUser(): void {
    const storedUser = localStorage.getItem('user_details');
    if (storedUser) {
      try {
        this.currentUserSubject.next(JSON.parse(storedUser));
      } catch (e) {
        this.logout();
      }
    }
  }

  login(credentials: { email: string; password: string }): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.apiUrl}/login`, credentials).pipe(
      tap(res => {
        if (res.success && res.data) {
          this.setSession(res.data);
        }
      })
    );
  }

  register(userData: any): Observable<ApiResponse<AuthResponse>> {
    this.logout();
    return this.http.post<ApiResponse<AuthResponse>>(`${this.apiUrl}/register`, userData);
  }

  private setSession(authData: AuthResponse): void {
    localStorage.setItem('auth_token', authData.token);
    const user: User = {
      userId: authData.userId,
      email: authData.email,
      fullName: authData.fullName,
      roles: authData.roles
    };
    localStorage.setItem('user_details', JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  logout(): void {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_details');
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  hasRole(role: string): boolean {
    const user = this.currentUserValue();
    if (!user || !user.roles) return false;
    const normalized = role.startsWith('ROLE_') ? role : `ROLE_${role}`;
    return user.roles.includes(normalized);
  }

  isCustomer(): boolean {
    return this.hasRole('ROLE_CUSTOMER');
  }

  isSeller(): boolean {
    return this.hasRole('ROLE_SELLER');
  }
}
```

---

## 4. JWT Authorization Interceptor (`jwt.interceptor.ts`)
Intercepts all outgoing HTTP calls and automatically attaches the `Authorization: Bearer <JWT>` header.

```typescript
import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = this.authService.getToken();
    if (token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
    return next.handle(request);
  }
}
```

---

## 5. Route Protection Guards (`auth.guard.ts` & `role.guard.ts`)

### `auth.guard.ts`
```typescript
import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (this.authService.isLoggedIn()) {
      return true;
    }

    this.router.navigate(['/auth/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }
}
```

### `role.guard.ts`
```typescript
import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const requiredRole = route.data['role'];
    if (!requiredRole || this.authService.hasRole(requiredRole)) {
      return true;
    }

    this.router.navigate(['/products']);
    return false;
  }
}
```

---

## 6. User Registration Component (`register.component.ts`)
Validates user input, role selection (`CUSTOMER` vs `SELLER`), and submits payload to Spring Boot REST API.

```typescript
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  registerForm: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      fullName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      role: ['CUSTOMER', [Validators.required]],
      phoneNumber: [''],
      addressLine1: [''],
      city: [''],
      state: [''],
      pinCode: ['']
    });
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      this.errorMessage = 'Please fill all required fields correctly (Name, valid Email, min 6-character Password).';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const payload = { ...this.registerForm.value };

    this.authService.register(payload).subscribe({
      next: () => {
        this.loading = false;
        // Redirect to login page with pre-filled email for demonstration
        this.router.navigate(['/auth/login'], {
          queryParams: { registered: 'true', email: payload.email }
        });
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.message || 'Registration failed. Please try again.';
      }
    });
  }
}
```

---

## 7. User Login Component (`login.component.ts`)
Authenticates credentials against backend and redirects to relevant portal.

```typescript
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;
  errorMessage = '';
  registeredSuccess = false;
  returnUrl = '/';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['registered'] === 'true') {
        this.registeredSuccess = true;
        const regEmail = params['email'] || '';
        if (regEmail) {
          this.loginForm.patchValue({ email: regEmail });
        }
      } else if (this.authService.isLoggedIn()) {
        this.redirectUser();
      }
    });
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        this.loading = false;
        this.redirectUser();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.message || 'Login failed. Please check your credentials.';
      }
    });
  }

  private redirectUser(): void {
    if (this.authService.isSeller()) {
      this.router.navigate(['/seller/dashboard']);
    } else {
      this.router.navigate([this.returnUrl !== '/' ? this.returnUrl : '/products']);
    }
  }

  fillCredentials(email: string): void {
    this.loginForm.patchValue({
      email: email,
      password: 'Password@123'
    });
  }
}
```

---

## 8. Product Catalog Component (`product-list.component.ts`)
Fetches products, enables search & category filtering, and handles stock-validated cart addition.

```typescript
import { Component, OnInit } from '@angular/core';
import { ProductService } from '../../../core/services/product.service';
import { CartService } from '../../../core/services/cart.service';
import { AuthService } from '../../../core/services/auth.service';
import { Category, Product } from '../../../shared/models/models';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  categories: Category[] = [];
  selectedCategoryId?: number;
  searchKeyword = '';
  loading = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private productService: ProductService,
    private cartService: CartService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadProducts();
  }

  loadCategories(): void {
    this.productService.getCategories().subscribe({
      next: (res) => {
        if (res.success) {
          this.categories = res.data;
        }
      }
    });
  }

  loadProducts(): void {
    this.loading = true;
    this.productService.getActiveProducts(this.selectedCategoryId, this.searchKeyword).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.success) {
          this.products = res.data;
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.message || 'Failed to load products';
      }
    });
  }

  selectCategory(categoryId?: number): void {
    this.selectedCategoryId = categoryId;
    this.loadProducts();
  }

  onSearch(): void {
    this.loadProducts();
  }

  addToCart(product: Product): void {
    if (!this.authService.isLoggedIn()) {
      alert('Please sign in as a Customer to add products to your cart.');
      return;
    }

    if (!this.authService.isCustomer()) {
      alert('Sellers cannot add products to cart. Please log in with a Customer account.');
      return;
    }

    this.cartService.addToCart(product.productId, 1).subscribe({
      next: () => {
        this.successMessage = `Added "${product.name}" to your cart!`;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        alert(err.message || 'Failed to add item to cart');
      }
    });
  }
}
```

---

## 9. Seller Analytics Dashboard (`seller-dashboard.component.ts`)
Displays real-time revenue metrics, order totals, catalog size, and automated low-stock warnings.

```typescript
import { Component, OnInit } from '@angular/core';
import { SellerService } from '../../../core/services/seller.service';
import { InventoryItem, SellerDashboardMetrics } from '../../../shared/models/models';

@Component({
  selector: 'app-seller-dashboard',
  templateUrl: './seller-dashboard.component.html',
  styleUrls: ['./seller-dashboard.component.css']
})
export class SellerDashboardComponent implements OnInit {
  metrics: SellerDashboardMetrics | null = null;
  lowStockItems: InventoryItem[] = [];
  loading = false;
  errorMessage = '';

  constructor(private sellerService: SellerService) {}

  ngOnInit(): void {
    this.fetchDashboardData();
  }

  fetchDashboardData(): void {
    this.loading = true;
    this.sellerService.getDashboardMetrics().subscribe({
      next: (res) => {
        if (res.success) {
          this.metrics = res.data;
        }
      },
      error: (err) => {
        this.errorMessage = err.message || 'Failed to load dashboard metrics';
      }
    });

    this.sellerService.getLowStockInventory().subscribe({
      next: (res) => {
        this.loading = false;
        if (res.success) {
          this.lowStockItems = res.data;
        }
      },
      error: () => {
        this.loading = false;
      }
    });
  }
}
```
