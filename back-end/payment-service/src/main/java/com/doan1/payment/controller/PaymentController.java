package com.doan1.payment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Placeholder controller for payment-service.
 *
 * Payment lifecycle (initiation, confirmation, webhooks, refunds, saga) is deferred.
 * All /api/payments/** endpoints return 501 Not Implemented until the feature is built.
 * The service remains in the reactor to hold the adapter skeleton and port allocation.
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @GetMapping
    public void notImplemented() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED,
                "Payment lifecycle chưa được triển khai. Xem DEC-015 và DECISIONS.md.");
    }
}
