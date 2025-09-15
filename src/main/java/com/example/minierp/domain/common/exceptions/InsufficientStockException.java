package com.example.minierp.domain.common.exceptions;

public class InsufficientStockException extends BusinessException {
    public InsufficientStockException(String productName, int requestedQty) {
        super("موجودی کافی برای محصول '" + productName + "' با تعداد " + requestedQty + " وجود ندارد.");
    }
}
