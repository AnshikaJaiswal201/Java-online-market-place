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

  // Auth
  { path: 'login', redirectTo: 'auth/login', pathMatch: 'full' },
  { path: 'register', redirectTo: 'auth/register', pathMatch: 'full' },
  { path: 'auth/login', component: LoginComponent },
  { path: 'auth/register', component: RegisterComponent },

  // Customer Routes
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

  // Seller Routes
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
