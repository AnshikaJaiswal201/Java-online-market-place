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
