package com.marketplace.common;

public final class Constants {

    private Constants() {
        // Prevent instantiation
    }

    public static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";
    public static final String ROLE_SELLER = "ROLE_SELLER";

    public static final String ORDER_STATUS_PLACED = "PLACED";
    public static final String ORDER_STATUS_CONFIRMED = "CONFIRMED";
    public static final String ORDER_STATUS_SHIPPED = "SHIPPED";
    public static final String ORDER_STATUS_DELIVERED = "DELIVERED";
    public static final String ORDER_STATUS_CANCELLED = "CANCELLED";

    public static final String PAYMENT_STATUS_PENDING = "PENDING";
    public static final String PAYMENT_STATUS_PAID = "PAID";
    public static final String PAYMENT_STATUS_FAILED = "FAILED";
}
