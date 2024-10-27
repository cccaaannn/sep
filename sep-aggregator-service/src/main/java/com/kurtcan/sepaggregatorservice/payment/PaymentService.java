package com.kurtcan.sepaggregatorservice.payment;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    List<Payment> getUserPayments();

    List<Payment> getUserPayments(UUID userId);
}
