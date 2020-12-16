package com.github.eltonsandre.sample.reactive;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnumUtils {

    public static <E extends Enum<E>> E safeValueOf(final Class<E> eEnum, final String name) {
        return safeValueOf(eEnum, name, null);
    }

    public static <E extends Enum<E>> E safeValueOf(final Class<E> enumClass, final Enum<?> enumParse) {
        return safeValueOf(enumClass, enumParse, null);
    }

    public static <E extends Enum<E>> E safeValueOf(final Class<E> enumClass, final String name, final E defaultEnum) {
        try {
            return Enum.valueOf(enumClass, name);
        } catch (final Exception ex) {
            return defaultEnum;
        }
    }

    public static <E extends Enum<E>> E safeValueOf(final Class<E> enumClass, final Enum<?> enumParse, final E defaultEnumReturn) {
        if (enumParse == null) {
            return defaultEnumReturn;
        }
        return safeValueOf(enumClass, enumParse.name(), defaultEnumReturn);
    }

    public static <E extends Enum<E>> Map<String, E> mapValues(final Class<E> enumClass) {
        return mapValues(enumClass.getEnumConstants(), E::name);
    }

    /**
     * Cria um Map de enum (Map<String key, Enum value>)
     *
     * @param values Enum.values()
     * @param <E>    tipo do Enum
     */
    public static <E extends Enum<E>> Map<String, E> mapValues(final E[] values) {
        return mapValues(values, E::name);
    }

    public static <E extends Enum<E>> Map<String, E> mapValues(final E[] values, final Function<E, String> keyMapper) {
        return Arrays.stream(values).collect(Collectors.toMap(keyMapper, e -> e));
    }

}
