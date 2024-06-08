package com.kurtcan.sepsearchservice.payment;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {
    List<Payment> match(int amount);
}
