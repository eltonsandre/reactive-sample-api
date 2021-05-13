package com.github.eltonsandre.sample.reactive;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.function.Consumer3;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Aspect
@Component
public class MappingAspect {

    private static final String POINTCUT = "@target(classRequestMapping) && @annotation(requestMapping) && execution(* *(..))";

    @Pointcut(POINTCUT)
    public void pointcutPost(RequestMapping classRequestMapping, PostMapping requestMapping) {
    }

    @Pointcut(POINTCUT)
    public void pointcutPut(RequestMapping classRequestMapping, PutMapping requestMapping) {
    }

    @Pointcut(POINTCUT)
    public void pointcutPatch(RequestMapping classRequestMapping, PatchMapping requestMapping) {
    }

    @Pointcut(POINTCUT)
    private void pointcutDelete(RequestMapping classRequestMapping, DeleteMapping requestMapping) {
    }

    @Around("pointcutPut(classRequestMapping,requestMapping) ||" +
            "pointcutPost(classRequestMapping,requestMapping) ||" +
            "pointcutPatch(classRequestMapping,requestMapping) ||" +
            "pointcutDelete(classRequestMapping,requestMapping)")
    public <A extends Annotation> Object aroundRequestMapping(ProceedingJoinPoint thisJoinPoint, RequestMapping classRequestMapping, A requestMapping) throws Throwable {
        log.info("requestMapping: {}", requestMapping);

        MethodSignature methodSignature = (MethodSignature) thisJoinPoint.getSignature();
        Annotation[][] annotationMatrix = methodSignature.getMethod().getParameterAnnotations();

        log.info("methodSignature: {}", methodSignature);

        final ServerHttpRequest block = ReactiveRequestContextHolder.getRequest().share().block();

        ReactiveRequestContextHolder.getRequest().subscribe(serverHttpRequest -> {
            log.info("ReactiveRequestContextHolder -> subscribe: {}", serverHttpRequest);
            String header = Objects.requireNonNull(serverHttpRequest.getHeaders().get("testHeaderStr")).get(0);
            log.info("print testHeader: {}", header);
        });

        return thisJoinPoint.proceed();
    }


}
