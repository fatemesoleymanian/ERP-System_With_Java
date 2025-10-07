package com.example.minierp.domain.common.exceptions;

public class NotFoundException extends BusinessException {
    public NotFoundException(Long id, String object) {
        super(object + " با شناسه " + id + " یافت نشد!");
    }
    public NotFoundException(String object) {
        super(object + " یافت نشد!");
    }
}
