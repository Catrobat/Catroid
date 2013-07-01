package org.catrobat.catroid.test;

import cucumber.api.android.RunWithCucumber;

import java.util.HashMap;
import java.util.Map;

@RunWithCucumber(features = "features/bricks/RepeatBrick.feature") // here you can configure which feature subfolder to use
public final class Cucumber {
    // The global state allows glue-class objects to share values with each other.
    private static final Map<String, Object> mGlobalState = new HashMap<String, Object>();
    static final String KEY_SOLO = "KEY_SOLO";
    static final String KEY_PROJECT = "KEY_PROJECT";
    static final String KEY_DEFAULT_BACKGROUND_NAME = "KEY_DEFAULT_BACKGROUND_NAME";
    static final String KEY_DEFAULT_PROJECT_NAME = "KEY_DEFAULT_PROJECT_NAME";
    static final String KEY_DEFAULT_SPRITE_NAME = "KEY_DEFAULT_SPRITE_NAME";
    static final String KEY_CURRENT_OBJECT = "KEY_CURRENT_OBJECT";
    static final String KEY_CURRENT_SCRIPT = "KEY_CURRENT_SCRIPT";
    static final String KEY_START_TIME = "KEY_START_TIME";

    private Cucumber() {
    }

    static void put(String key, Object value) {
        mGlobalState.put(key, value);
    }

    static Object get(String key) {
        return mGlobalState.get(key);
    }
}
