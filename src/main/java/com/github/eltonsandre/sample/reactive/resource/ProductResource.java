package com.github.eltonsandre.sample.reactive.resource;

import com.github.eltonsandre.sample.reactive.model.Product;
import com.github.eltonsandre.sample.reactive.repository.ProductRepository;
import com.github.eltonsandre.sample.reactive.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author eltonsandre
 */
@RestController
@RequestMapping("products")
@RequiredArgsConstructor
public class ProductResource {

    private final ProductService service;
    private final ProductRepository repository;

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Product> get(@PathVariable final String id) {
        return this.service.findProduct(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<Product> getAll() {
        return this.service.findProducts();
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Product> create(@RequestBody final Product product) {
        return this.repository.save(product);
    }


    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final String id) {
        this.repository.deleteById(id);
    }


}
