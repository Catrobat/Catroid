package org.catrobat.catroid.test.utils;

import java.util.Collection;

public final class ParameterizedTestData {

    private ParameterizedTestData() {
    }

    public static <T extends Collection<?>> T requireNonEmpty(T testData,
            Class<?> testClass, String sourceDescription) {
        if (testData == null || testData.isEmpty()) {
            throw new AssertionError("No parameterized test data found for "
                    + testClass.getName() + ". Source: " + sourceDescription);
        }

        return testData;
    }
}
