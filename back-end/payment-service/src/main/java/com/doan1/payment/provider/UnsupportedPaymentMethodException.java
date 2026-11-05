package com.doan1.payment.provider;

public class UnsupportedPaymentMethodException extends RuntimeException {
    public UnsupportedPaymentMethodException(String providerId, String method) {
        super("Provider '%s' does not support payment method '%s'".formatted(providerId, method));
    }
}
