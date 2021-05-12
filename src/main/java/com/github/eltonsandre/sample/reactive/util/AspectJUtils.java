package com.github.eltonsandre.sample.reactive.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AspectJUtils {

    public static Annotation[][] getParameterAnnotationsMethod(final JoinPoint joinPoint) {
        return AspectJUtils.getMethod(joinPoint).getParameterAnnotations();
    }

    /**
     * Recupera a annotation do metodo a partir do JoinPoint.
     */
    public static <A extends Annotation> A getAnnotationMethod(final JoinPoint joinPoint, final Class<A> aClass) {
        return getMethod(joinPoint).getAnnotation(aClass);
    }

    /**
     * Recupera metodo a partir do JoinPoint.
     */
    public static Method getMethod(final JoinPoint joinPoint) {
        final MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        return ms.getMethod();
    }

    /**
     * Recupera um map com nome e valor do parametros do metodo
     */
    public static Map<String, Object> getParametersMapMethod(final JoinPoint joinPoint) {
        final Object[] args = joinPoint.getArgs();

        final CodeSignature methodSignature = (CodeSignature) joinPoint.getSignature();
        final String[] paramNames = methodSignature.getParameterNames();

        return IntStream.range(0, paramNames.length).boxed()
                .collect(Collectors.toMap(it -> paramNames[it], it -> args[it]));
    }

    /**
     * Recupera o valor do parametro anotado (Class<A extends Annotation>)
     */
    public static <A extends Annotation> Optional<Object> getParameterValueByAnnotation(final Map<String, Object> parametersMap,
                                                                                        final Class<A> aClass) {
        return filterByAnnotation(parametersMap, aClass)
                .map(Map.Entry::getValue)
                .findFirst();
    }

    /**
     * Recupera o valor do parametro anotado (Class<A extends Annotation>)
     */
    public static <A extends Annotation> Optional<Object> getParameterValueByAnnotation(final JoinPoint joinPoint,
                                                                                        final Class<A> aClass) {
        return filterByAnnotation(joinPoint, aClass)
                .map(Map.Entry::getValue)
                .findFirst();
    }

    /**
     * Recupera o valor do parametro anotado (Class<A extends Annotation>)
     */
    public static <A extends Annotation> Map<String, Object> getParameterValuesByAnnotationO(final JoinPoint joinPoint,
                                                                                             final Class<A> aClass) {
        return filterByAnnotation(joinPoint, aClass)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static <A extends Annotation> Stream<Map.Entry<String, Object>> filterByAnnotation(final JoinPoint joinPoint,
                                                                                               final Class<A> aClass) {
        return filterByAnnotation(getParametersMapMethod(joinPoint), aClass);
    }

    private static <A extends Annotation> Stream<Map.Entry<String, Object>> filterByAnnotation(final Map<String, Object> parametersMap,
                                                                                               final Class<A> aClass) {
        return parametersMap.entrySet().stream()
                .filter(param -> Objects.nonNull(param.getValue().getClass().getAnnotation(aClass)));
    }

}


