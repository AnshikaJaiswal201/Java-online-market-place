import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProductService } from '../../../core/services/product.service';
import { Category, Product } from '../../../shared/models/models';

@Component({
  selector: 'app-seller-products',
  templateUrl: './seller-products.component.html',
  styleUrls: ['./seller-products.component.css']
})
export class SellerProductsComponent implements OnInit {
  products: Product[] = [];
  categories: Category[] = [];
  loading = false;
  showModal = false;
  isEditing = false;
  selectedProductId?: number;
  productForm: FormGroup;
  errorMessage = '';
  successMessage = '';

  constructor(
    private productService: ProductService,
    private fb: FormBuilder
  ) {
    this.productForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: [''],
      price: ['', [Validators.required, Validators.min(1)]],
      sku: ['', [Validators.required]],
      imageUrl: [''],
      categoryId: ['', [Validators.required]],
      initialStock: [10, [Validators.required, Validators.min(0)]],
      lowStockThreshold: [5, [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    this.fetchSellerProducts();
    this.fetchCategories();
  }

  fetchSellerProducts(): void {
    this.loading = true;
    this.productService.getSellerProducts().subscribe({
      next: (res) => {
        this.loading = false;
        if (res.success) {
          this.products = res.data;
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.message || 'Failed to load seller products';
      }
    });
  }

  fetchCategories(): void {
    this.productService.getCategories().subscribe({
      next: (res) => {
        if (res.success) {
          this.categories = res.data;
        }
      }
    });
  }

  openAddModal(): void {
    this.isEditing = false;
    this.selectedProductId = undefined;
    this.productForm.reset({
      price: '',
      initialStock: 10,
      lowStockThreshold: 5
    });
    this.showModal = true;
  }

  openEditModal(product: Product): void {
    this.isEditing = true;
    this.selectedProductId = product.productId;
    this.productForm.patchValue({
      name: product.name,
      description: product.description,
      price: product.price,
      sku: product.sku,
      imageUrl: product.imageUrl,
      categoryId: product.categoryId,
      initialStock: product.stockQuantity,
      lowStockThreshold: product.lowStockThreshold
    });
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.errorMessage = '';
  }

  onSubmit(): void {
    if (this.productForm.invalid) {
      return;
    }

    const payload = this.productForm.value;

    if (this.isEditing && this.selectedProductId) {
      this.productService.updateProduct(this.selectedProductId, payload).subscribe({
        next: () => {
          this.closeModal();
          this.fetchSellerProducts();
          this.showSuccess('Product updated successfully!');
        },
        error: (err) => this.errorMessage = err.message
      });
    } else {
      this.productService.createProduct(payload).subscribe({
        next: () => {
          this.closeModal();
          this.fetchSellerProducts();
          this.showSuccess('Product added to catalog with initial stock!');
        },
        error: (err) => this.errorMessage = err.message
      });
    }
  }

  toggleStatus(product: Product): void {
    const action = product.isActive === 1 ? 'deactivate (soft-delete)' : 'activate';
    if (confirm(`Are you sure you want to ${action} "${product.name}"?`)) {
      this.productService.toggleProductStatus(product.productId).subscribe({
        next: (res) => {
          if (res.success) {
            product.isActive = res.data.isActive;
            this.showSuccess(`Product ${product.isActive === 1 ? 'activated' : 'deactivated'} successfully.`);
          }
        },
        error: (err) => alert(err.message)
      });
    }
  }

  private showSuccess(msg: string): void {
    this.successMessage = msg;
    setTimeout(() => this.successMessage = '', 3000);
  }
}
