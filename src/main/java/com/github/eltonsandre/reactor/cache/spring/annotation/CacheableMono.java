package com.github.eltonsandre.reactor.cache.spring.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheableMono {

    @AliasFor("cacheName")
    String value() default "";

    @AliasFor("value")
    String cacheName() default "";

    String key() default "";

    String ttl() default "-PT1S";

}
