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

export interface ProductRequest {
  name: string;
  description?: string;
  price: number;
  sku: string;
  imageUrl?: string;
  categoryId: number;
  initialStock: number;
  lowStockThreshold: number;
}

export interface InventoryItem {
  inventoryId: number;
  productId: number;
  productName: string;
  sku: string;
  price: number;
  stockQuantity: number;
  lowStockThreshold: number;
  lowStock: boolean;
  lastRestockedAt: string;
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
  sellerName: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export interface Order {
  orderId: number;
  orderNumber: string;
  customerId: number;
  customerName: string;
  customerEmail: string;
  totalAmount: number;
  orderStatus: 'PLACED' | 'CONFIRMED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
  shippingAddress: string;
  paymentMethod: string;
  paymentStatus: string;
  createdAt: string;
  items: OrderItem[];
}

export interface CheckoutRequest {
  shippingAddress: string;
  paymentMethod: string;
}

export interface SellerDashboardMetrics {
  totalRevenueInr: number;
  totalOrdersCount: number;
  activeProductsCount: number;
  lowStockCount: number;
}
