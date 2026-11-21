import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, InventoryItem, Order, SellerDashboardMetrics } from '../../shared/models/models';

@Injectable({
  providedIn: 'root'
})
export class SellerService {
  private apiUrl = `${environment.apiUrl}/seller`;

  constructor(private http: HttpClient) {}

  getDashboardMetrics(): Observable<ApiResponse<SellerDashboardMetrics>> {
    return this.http.get<ApiResponse<SellerDashboardMetrics>>(`${this.apiUrl}/dashboard/metrics`);
  }

  getInventory(): Observable<ApiResponse<InventoryItem[]>> {
    return this.http.get<ApiResponse<InventoryItem[]>>(`${this.apiUrl}/inventory`);
  }

  getLowStockInventory(): Observable<ApiResponse<InventoryItem[]>> {
    return this.http.get<ApiResponse<InventoryItem[]>>(`${this.apiUrl}/inventory/low-stock`);
  }

  updateStock(inventoryId: number, stockQuantity: number, lowStockThreshold?: number): Observable<ApiResponse<InventoryItem>> {
    return this.http.put<ApiResponse<InventoryItem>>(`${this.apiUrl}/inventory/${inventoryId}`, {
      stockQuantity,
      lowStockThreshold
    });
  }

  getSellerOrders(): Observable<ApiResponse<Order[]>> {
    return this.http.get<ApiResponse<Order[]>>(`${this.apiUrl}/orders`);
  }

  updateOrderStatus(orderId: number, orderStatus: string): Observable<ApiResponse<Order>> {
    return this.http.patch<ApiResponse<Order>>(`${this.apiUrl}/orders/${orderId}/status`, {
      orderStatus
    });
  }
}
