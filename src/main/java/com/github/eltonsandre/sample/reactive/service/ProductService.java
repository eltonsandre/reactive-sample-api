package com.github.eltonsandre.sample.reactive.service;

import com.github.eltonsandre.reactor.cache.spring.annotation.CacheableFlux;
import com.github.eltonsandre.reactor.cache.spring.annotation.CacheableMono;
import com.github.eltonsandre.sample.reactive.model.Product;
import com.github.eltonsandre.sample.reactive.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ReactiveRedisOperations<String, Object> redis;


    @CacheableMono(cacheName = "product")
    public Mono<Product> findProduct(final String id) {
        return this.repository.findById(id);
    }

    @CacheEvict(value = "product", allEntries = true)
    @CacheableFlux(cacheName = "allProducts", ttl = "${caches.product.time-to-live}")
    public Flux<Product> findProducts() {
        return this.repository.findAll();
    }


}






