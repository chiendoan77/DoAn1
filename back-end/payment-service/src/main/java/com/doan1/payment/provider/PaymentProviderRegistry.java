package com.doan1.payment.provider;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class PaymentProviderRegistry {
    private final Map<String, PaymentProviderAdapter> adaptersByProviderId;

    public PaymentProviderRegistry(List<PaymentProviderAdapter> adapters) {
        this.adaptersByProviderId = adapters.stream()
                .collect(Collectors.toUnmodifiableMap(
                        PaymentProviderAdapter::providerId,
                        Function.identity()));
    }

    public PaymentProviderAdapter require(String providerId, String method) {
        PaymentProviderAdapter adapter = adaptersByProviderId.get(providerId);
        if (adapter == null || !adapter.supportedMethods().contains(method)) {
            throw new UnsupportedPaymentMethodException(providerId, method);
        }
        return adapter;
    }

    public Set<String> providerIds() {
        return adaptersByProviderId.keySet();
    }
}
