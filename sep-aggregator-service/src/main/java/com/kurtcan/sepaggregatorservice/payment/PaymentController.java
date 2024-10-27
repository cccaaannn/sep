package com.kurtcan.sepaggregatorservice.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @QueryMapping
    public List<Payment> payments() {
        return paymentService.getUserPayments();
    }

}
