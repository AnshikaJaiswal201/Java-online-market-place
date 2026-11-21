import { Component, OnInit } from '@angular/core';
import { SellerService } from '../../../core/services/seller.service';
import { Order } from '../../../shared/models/models';

@Component({
  selector: 'app-seller-orders',
  templateUrl: './seller-orders.component.html',
  styleUrls: ['./seller-orders.component.css']
})
export class SellerOrdersComponent implements OnInit {
  orders: Order[] = [];
  loading = false;
  errorMessage = '';
  successMessage = '';

  statusOptions = ['PLACED', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

  constructor(private sellerService: SellerService) {}

  ngOnInit(): void {
    this.fetchOrders();
  }

  fetchOrders(): void {
    this.loading = true;
    this.sellerService.getSellerOrders().subscribe({
      next: (res) => {
        this.loading = false;
        if (res.success) {
          this.orders = res.data;
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.message || 'Failed to load seller orders';
      }
    });
  }

  onStatusChange(order: Order, newStatus: string): void {
    if (order.orderStatus === newStatus) {
      return;
    }

    if (confirm(`Change status of order ${order.orderNumber} to "${newStatus}"?`)) {
      this.sellerService.updateOrderStatus(order.orderId, newStatus).subscribe({
        next: (res) => {
          if (res.success) {
            order.orderStatus = res.data.orderStatus;
            this.successMessage = `Order ${order.orderNumber} status updated to ${newStatus}`;
            setTimeout(() => this.successMessage = '', 3000);
          }
        },
        error: (err) => alert(err.message)
      });
    }
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'DELIVERED': return 'badge-success';
      case 'SHIPPED':
      case 'CONFIRMED': return 'badge-info';
      case 'PLACED': return 'badge-warning';
      case 'CANCELLED': return 'badge-danger';
      default: return 'badge-neutral';
    }
  }
}
