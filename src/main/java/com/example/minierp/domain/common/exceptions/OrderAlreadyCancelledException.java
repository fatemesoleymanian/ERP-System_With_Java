package com.example.minierp.domain.common.exceptions;

public class OrderAlreadyCancelledException extends BusinessException {
    public OrderAlreadyCancelledException(Long orderId) {
        super("سفارش " + orderId + " قبلاً لغو شده است.");
    }
}
