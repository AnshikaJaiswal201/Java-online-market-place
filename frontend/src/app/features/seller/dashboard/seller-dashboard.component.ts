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
