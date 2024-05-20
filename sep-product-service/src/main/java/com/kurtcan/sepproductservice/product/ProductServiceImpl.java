package com.kurtcan.sepproductservice.product;

import com.kurtcan.sepproductservice.product.request.ProductAdd;
import com.kurtcan.sepproductservice.product.request.ProductUpdate;
import com.kurtcan.sepproductservice.shared.event.JsonEventPublisher;
import com.kurtcan.sepproductservice.shared.event.SimpleEvent;
import com.kurtcan.sepproductservice.shared.exception.ResourceNotFoundException;
import com.kurtcan.sepproductservice.shared.pagaination.PageImpl;
import com.kurtcan.sepproductservice.shared.specification.SearchCriteriaBuilder;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
@Builder
@RequiredArgsConstructor
@CacheConfig(cacheNames = "products")
public class ProductServiceImpl implements ProductService {

    private final JsonEventPublisher eventPublisher;
    private final ProductRepository repository;
    private final ModelMapper mapper;

    @Override
    public Product getProduct(UUID id) {
        return repository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    @Cacheable
    public PageImpl<Product> searchProduct(String criteriaStr, PageRequest pageRequest) {
        var specs = Arrays.stream(criteriaStr.split(","))
                .map(SearchCriteriaBuilder::build)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(ProductSpecification::new)
                .toList();

        if (specs.isEmpty()) {
            return PageImpl.from(repository.findAll(pageRequest));
        }

        Specification<Product> finalSpec = specs.getFirst();
        for (int i = 1; i < specs.size(); i++) {
            finalSpec = Specification.where(finalSpec).and(specs.get(i));
        }

        return PageImpl.from(repository.findAll(finalSpec, pageRequest));
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public Product addProduct(ProductAdd productAdd) {
        Product product = mapper.map(productAdd, Product.class);
        product = repository.save(product);
        repository.flush();
        eventPublisher.publish(ProductEventTopic.CREATED, SimpleEvent.fromEntity(product));
        return product;
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public Product updateProduct(UUID id, ProductUpdate productUpdate) {
        Product product = repository.findById(id).orElseThrow(ResourceNotFoundException::new);
        mapper.map(productUpdate, product);
        repository.save(product);
        repository.flush();
        eventPublisher.publish(ProductEventTopic.UPDATED, SimpleEvent.fromEntity(product));
        return product;
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public void deleteProduct(UUID id) {
        repository.findById(id).orElseThrow(ResourceNotFoundException::new);
        repository.deleteById(id);
        repository.flush();
        eventPublisher.publish(ProductEventTopic.DELETED, SimpleEvent.builder().id(id).build());
    }

}
