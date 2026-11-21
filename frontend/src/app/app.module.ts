import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

// Interceptors
import { JwtInterceptor } from './core/interceptors/jwt.interceptor';
import { ErrorInterceptor } from './core/interceptors/error.interceptor';

// Shared Components & Pipes
import { NavbarComponent } from './shared/components/navbar/navbar.component';
import { FooterComponent } from './shared/components/footer/footer.component';
import { InrCurrencyPipe } from './shared/pipes/inr-currency.pipe';

// Auth Features
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';

// Customer Features
import { ProductListComponent } from './features/customer/catalog/product-list.component';
import { CartViewComponent } from './features/customer/cart/cart-view.component';
import { CheckoutComponent } from './features/customer/checkout/checkout.component';
import { OrderHistoryComponent } from './features/customer/orders/order-history.component';

// Seller Features
import { SellerDashboardComponent } from './features/seller/dashboard/seller-dashboard.component';
import { SellerProductsComponent } from './features/seller/products/seller-products.component';
import { SellerInventoryComponent } from './features/seller/inventory/seller-inventory.component';
import { SellerOrdersComponent } from './features/seller/orders/seller-orders.component';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    FooterComponent,
    InrCurrencyPipe,
    LoginComponent,
    RegisterComponent,
    ProductListComponent,
    CartViewComponent,
    CheckoutComponent,
    OrderHistoryComponent,
    SellerDashboardComponent,
    SellerProductsComponent,
    SellerInventoryComponent,
    SellerOrdersComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
