package com.github.eltonsandre.reactor.cache.spring.annotation;

import com.github.eltonsandre.reactor.cache.spring.interceptor.CacheableFluxSupport;
import com.github.eltonsandre.reactor.cache.spring.interceptor.CacheableMonoSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableCaching
@Import({ CacheableMonoSupport.class, CacheableFluxSupport.class})
public @interface EnableReactiveCaching {

}
