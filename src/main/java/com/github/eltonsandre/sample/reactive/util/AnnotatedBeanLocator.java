package com.github.eltonsandre.sample.reactive.util;

import com.github.eltonsandre.sample.reactive.resource.TestResource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class AnnotatedBeanLocator {


    private final Map<String, String> routesIgnoredMap;

    public AnnotatedBeanLocator() {
        this.routesIgnoredMap = this.processAnnotation();
    }


    @SneakyThrows
    private Map<String, String> processAnnotation() {
        final var scanner = this.createComponentScanner(RequestMapping.class);

        final var routesMap = new HashMap<String, String>();
        for (final BeanDefinition bd : scanner.findCandidateComponents(TestResource.class.getPackageName())) {
            log.info("BeanDefinition: {}", bd);
            routesMap.putAll(this.processMethodsAnnotatedByClass(Class.forName(bd.getBeanClassName())));
        }
        return routesMap;
    }

    private ClassPathScanningCandidateComponentProvider createComponentScanner(final Class<? extends Annotation> annotationClass) {
        final var provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(annotationClass));
        return provider;
    }

    private Map<String, String> processMethodsAnnotatedByClass(final Class<?> targetClass) {
        final RequestMapping annotationTyped = AnnotationUtils.getAnnotation(targetClass, RequestMapping.class);

        final var routesMap = new HashMap<String, String>();
        final String requestMappingBase = ArrayUtils.isNotEmpty(annotationTyped.value()) ? annotationTyped.value()[0] : StringUtils.EMPTY;

        ReflectionUtils.doWithMethods(targetClass, method -> {
                    final var key = targetClass.getSimpleName().concat(".").concat(method.getName());

                    final String[] valueRequestMapping = this.getValueRequestMapping(method);
                    final var value = ArrayUtils.isNotEmpty(valueRequestMapping) ? valueRequestMapping[0] : StringUtils.EMPTY;

                    log.debug("processMethodsAnnotatedByClass: {}: {}", key, value);

                    routesMap.put(key, UriComponentsBuilder.newInstance().pathSegment(requestMappingBase).encode().pathSegment(value).toUriString());

                }, filterMethod -> AnnotationUtils.findAnnotation(filterMethod, GetMapping.class) != null
        );

        return routesMap;
    }

    private String[] getValueRequestMapping(final Method method) {
        Arrays.asList(method.getDeclaredAnnotations()).stream()
                .filter()
                .findFirst();

        final GetMapping annotation = AnnotationUtils.findAnnotation(method, GetMapping.class);
    }


}
