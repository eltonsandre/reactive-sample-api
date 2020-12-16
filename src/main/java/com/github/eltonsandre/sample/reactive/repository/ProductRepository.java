package com.github.eltonsandre.sample.reactive.repository;


import com.github.eltonsandre.sample.reactive.model.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends ReactiveMongoRepository<Product, String> {

}
