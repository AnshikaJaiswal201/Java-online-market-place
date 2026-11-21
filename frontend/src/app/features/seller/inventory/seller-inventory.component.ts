import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SellerService } from '../../../core/services/seller.service';
import { InventoryItem } from '../../../shared/models/models';

@Component({
  selector: 'app-seller-inventory',
  templateUrl: './seller-inventory.component.html',
  styleUrls: ['./seller-inventory.component.css']
})
export class SellerInventoryComponent implements OnInit {
  inventory: InventoryItem[] = [];
  loading = false;
  selectedItem: InventoryItem | null = null;
  stockForm: FormGroup;
  showModal = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private sellerService: SellerService,
    private fb: FormBuilder
  ) {
    this.stockForm = this.fb.group({
      stockQuantity: ['', [Validators.required, Validators.min(0)]],
      lowStockThreshold: ['', [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    this.fetchInventory();
  }

  fetchInventory(): void {
    this.loading = true;
    this.sellerService.getInventory().subscribe({
      next: (res) => {
        this.loading = false;
        if (res.success) {
          this.inventory = res.data;
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.message || 'Failed to load inventory';
      }
    });
  }

  openRestockModal(item: InventoryItem): void {
    this.selectedItem = item;
    this.stockForm.patchValue({
      stockQuantity: item.stockQuantity,
      lowStockThreshold: item.lowStockThreshold
    });
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.selectedItem = null;
  }

  onSubmit(): void {
    if (this.stockForm.invalid || !this.selectedItem) {
      return;
    }

    const { stockQuantity, lowStockThreshold } = this.stockForm.value;

    this.sellerService.updateStock(this.selectedItem.inventoryId, stockQuantity, lowStockThreshold).subscribe({
      next: (res) => {
        if (res.success) {
          this.closeModal();
          this.fetchInventory();
          this.successMessage = `Inventory updated for "${res.data.productName}"`;
          setTimeout(() => this.successMessage = '', 3000);
        }
      },
      error: (err) => alert(err.message)
    });
  }
}
