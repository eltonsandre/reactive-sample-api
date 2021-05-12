package com.github.eltonsandre.sample.reactive;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.util.Objects;

@Slf4j
@Aspect
@Component
public class MappingAspect {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping)"
    )
    public void pointcutMapping() {
//    public void pointcutMapping(@Autowired ServerWebExchange exchange) {
//        log.info("exchange: {}", exchange);
    }

    @Pointcut("@target(classRequestMapping) && @annotation(requestMapping) && execution(* *(..))")
    public void controller(RequestMapping classRequestMapping, RequestMapping requestMapping) {

        log.info("Pointcut->controller: classRequestMapping: {}, requestMapping: {}", classRequestMapping, requestMapping);
    }

    @Before("controller(classRequestMapping, requestMapping)")
    public void advice(JoinPoint thisJoinPoint, RequestMapping classRequestMapping, RequestMapping requestMapping) {

        log.info("before->advice: classRequestMapping: {}, requestMapping: {}", classRequestMapping, requestMapping);
    }

    @Before("pointcutMapping()")
    public void logRequestBody(JoinPoint thisJoinPoint) {
        MethodSignature methodSignature = (MethodSignature) thisJoinPoint.getSignature();
        Annotation[][] annotationMatrix = methodSignature.getMethod().getParameterAnnotations();

        log.info("methodSignature: {}", methodSignature);

//        final ServerHttpRequest block = Mono.deferContextual(Mono::just)
//                .map(contextView -> contextView.get(ServerWebExchange.class).getRequest()).share().block();

        ReactiveRequestContextHolder.getRequest().subscribe(serverHttpRequest -> {
            log.info("ReactiveRequestContextHolder -> subscribe: {}", serverHttpRequest);
            String header = Objects.requireNonNull(serverHttpRequest.getHeaders().get("testHeaderStr")).get(0);
            log.info("print testHeader: {}", header);
        });

    }


}
