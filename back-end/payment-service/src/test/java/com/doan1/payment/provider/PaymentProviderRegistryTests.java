package com.doan1.payment.provider;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class PaymentProviderRegistryTests {
    @Test
    void selectsProviderThatSupportsRequestedMethod() {
        PaymentProviderAdapter cardProvider = new FakeAdapter("provider-a", Set.of("card"));
        PaymentProviderAdapter qrProvider = new FakeAdapter("provider-b", Set.of("qr"));
        PaymentProviderRegistry registry = new PaymentProviderRegistry(List.of(cardProvider, qrProvider));

        assertSame(cardProvider, registry.require("provider-a", "card"));
        assertSame(qrProvider, registry.require("provider-b", "qr"));
        assertSame(cardProvider, registry.require("provider-a", "card"));
    }

    @Test
    void rejectsUnknownProviderOrUnsupportedMethod() {
        PaymentProviderRegistry registry = new PaymentProviderRegistry(
                List.of(new FakeAdapter("provider-a", Set.of("card"))));

        assertThrows(UnsupportedPaymentMethodException.class, () -> registry.require("missing", "card"));
        assertThrows(UnsupportedPaymentMethodException.class, () -> registry.require("provider-a", "qr"));
    }

    private record FakeAdapter(String providerId, Set<String> supportedMethods)
            implements PaymentProviderAdapter {
    }
}
