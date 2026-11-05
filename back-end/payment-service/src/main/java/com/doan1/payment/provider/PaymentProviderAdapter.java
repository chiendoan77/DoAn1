package com.doan1.payment.provider;

import java.util.Set;

public interface PaymentProviderAdapter {
    String providerId();

    Set<String> supportedMethods();
}
