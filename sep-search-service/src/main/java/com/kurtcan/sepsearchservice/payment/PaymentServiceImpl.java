package com.kurtcan.sepsearchservice.payment;

import com.kurtcan.sepsearchservice.shared.elasticsearch.SimpleElasticsearchClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final SimpleElasticsearchClient elasticsearchClient;

    @Override
    public List<Payment> match(int amount) {
        return elasticsearchClient.match(PaymentElasticIndex.NAME, "amount", String.valueOf(amount), Payment.class).orElse(List.of());
    }
}
