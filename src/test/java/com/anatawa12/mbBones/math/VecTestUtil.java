package com.anatawa12.mbBones.math;

import static org.junit.jupiter.api.Assertions.fail;

public class VecTestUtil {
    public static void assertEquals(Vec3f expected, Vec3f actual, float delta) {
        if (Math.abs(expected.x - actual.x) < delta &&
                Math.abs(expected.y - actual.y) < delta &&
                Math.abs(expected.z - actual.z) < delta)
            return;

        fail(formatValues(expected, actual));
    }

    public static void assertEquals(Vec4f expected, Vec4f actual, float delta) {
        if (Math.abs(expected.x - actual.x) < delta &&
                Math.abs(expected.y - actual.y) < delta &&
                Math.abs(expected.z - actual.z) < delta &&
                Math.abs(expected.w - actual.w) < delta)
            return;

        fail(formatValues(expected, actual));
    }

    static String formatValues(Object expected, Object actual) {
        String expectedString = expected.toString();
        String actualString = actual.toString();
        if (expectedString.equals(actualString)) {
            return String.format("expected: %s but was: %s", formatClassAndValue(expected, expectedString),
                    formatClassAndValue(actual, actualString));
        }
        return String.format("expected: <%s> but was: <%s>", expectedString, actualString);
    }

    private static String formatClassAndValue(Object value, String valueString) {
        String classAndHash = value.getClass().getName() + Integer.toHexString(System.identityHashCode(value));
        return classAndHash + "<" + valueString + ">";
    }

}
