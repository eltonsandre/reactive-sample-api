package com.github.eltonsandre.sample.reactive;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnumUtilsTest {

    static Stream<Arguments> safeValueOfParams() {
        return Stream.of(
                Arguments.of(AnotherEnum.TEST_1, (AnyEnum) null, AnyEnum.TEST_1),
                Arguments.of(null, AnyEnum.DEFAULT, AnyEnum.DEFAULT),
                Arguments.of(null, (AnyEnum) null, (AnyEnum) null)
        );
    }

    static Stream<Arguments> safeValueOfParamsString() {
        return Stream.of(
                Arguments.of("TEST_1", (AnyEnum) null, AnyEnum.TEST_1),
                Arguments.of(null, AnyEnum.DEFAULT, AnyEnum.DEFAULT),
                Arguments.of((String) null, (AnyEnum) null, (AnyEnum) null)
        );
    }

    static Stream<Arguments> safeValueOfByNameString() {
        return Stream.of(
                Arguments.of("TEST_1", AnyEnum.TEST_1),
                Arguments.of("TEST_2", AnyEnum.TEST_2),
                Arguments.of((String) null, (AnyEnum) null)
        );
    }

    @MethodSource("safeValueOfParams")
    @ParameterizedTest(name = "Another: {0}, default: {1}, expected: {2}")
    void safeValueOfParams3Enum(final AnotherEnum anotherEnum, final AnyEnum defaultEnum, final AnyEnum expected) {
        final var actual = EnumUtils.safeValueOf(AnyEnum.class, anotherEnum, defaultEnum);
        assertEquals(expected, actual);
    }

    @MethodSource("safeValueOfParamsString")
    @ParameterizedTest(name = "Another: {0}, default: {1}, expected: {2}")
    void testSafeValueOfParams3String(final String nameEnum, final AnyEnum defaultEnum, final AnyEnum expected) {
        final var actual = EnumUtils.safeValueOf(AnyEnum.class, nameEnum, defaultEnum);
        assertEquals(expected, actual);
    }

    @MethodSource("safeValueOfByNameString")
    @ParameterizedTest(name = "Another: {0}, expected: {1}")
    void testSafeValueOfParams2String(final String nameEnum, final AnyEnum expected) {
        final var actual = EnumUtils.safeValueOf(AnyEnum.class, nameEnum);
        assertEquals(expected, actual);
    }


    @Test
    void mapValues() {
        final var actual = EnumUtils.mapValues(AnyEnum.class);
        assertEquals(AnyEnum.TEST_1, actual.get("TEST_1"));
        assertEquals(AnyEnum.TEST_2, actual.get("TEST_2"));
        assertEquals(AnyEnum.DEFAULT, actual.get("DEFAULT"));
        assertEquals(null, actual.get("unknow"));
        assertEquals(null, actual.get(""));
        assertEquals(null, actual.get(null));
    }

    @Test
    void mapValues2() {
        final var actual = EnumUtils.mapValues(AnotherEnum.values());
        assertEquals(AnotherEnum.TEST_1, actual.get("TEST_1"));
        assertEquals(AnotherEnum.TEST_2, actual.get("TEST_2"));
        assertEquals(AnotherEnum.UNKNOWN, actual.get("UNKNOWN"));
        assertEquals(null, actual.get("unknow"));
        assertEquals(null, actual.get(""));
        assertEquals(null, actual.get(null));
    }

    @Test
    void mapValues3() {
        final var actual = EnumUtils.mapValues(AnyEnum.values(), AnyEnum::getLabel);
        assertEquals(AnyEnum.TEST_1, actual.get("test1"));
        assertEquals(AnyEnum.TEST_2, actual.get("test2"));
        assertEquals(AnyEnum.DEFAULT, actual.get("default"));
        assertEquals(null, actual.get("unknow"));
        assertEquals(null, actual.get(""));
        assertEquals(null, actual.get(null));
    }


    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public enum AnyEnum {
        TEST_1("test1"),
        TEST_2("test2"),
        DEFAULT("default");

        private final String label;
    }

    public enum AnotherEnum {
        TEST_1, TEST_2, UNKNOWN
    }

}
