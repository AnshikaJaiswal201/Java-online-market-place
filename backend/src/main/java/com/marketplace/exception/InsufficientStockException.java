package com.marketplace.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String sku, int availableStock, int requestedQuantity) {
        super(String.format("Insufficient stock for SKU '%s'. Available: %d, Requested: %d",
                sku, availableStock, requestedQuantity));
    }
}
