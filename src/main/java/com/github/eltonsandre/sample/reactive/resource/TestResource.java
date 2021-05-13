package com.github.eltonsandre.sample.reactive.resource;

import com.github.eltonsandre.sample.reactive.ReactiveRequestContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

@RestController
@RequestMapping("test")
public class TestResource {

    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    public Mono<?> testpost() {

        final ContextView block1 = Mono.deferContextual(Mono::just)
                .map(ctx -> ctx).share().block();

        final ServerHttpRequest block = ReactiveRequestContextHolder.getRequest().share().block();
        return Mono.empty();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<?> testput() {
        return Mono.empty();
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<?> testpatch() {
        return Mono.empty();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<?> testdel() {
        return Mono.empty();
    }

}