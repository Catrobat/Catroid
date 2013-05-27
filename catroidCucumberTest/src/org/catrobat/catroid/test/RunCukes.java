package org.catrobat.catroid.test;

import cucumber.api.android.RunWithCucumber;

import java.util.HashMap;
import java.util.Map;

@RunWithCucumber(features = "features") // configure which feature subfolder to use
public final class RunCukes {
    // By default, all glue/step classes are instantiated.
    // With this map they can share common values/objects.
    private static final Map<String, Object> mGlobalState = new HashMap<String, Object>();
    static final String KEY_SOLO = "KEY_SOLO";
    static final String KEY_PROJECT = "KEY_PROJECT";
    static final String KEY_DEFAULT_BACKGROUND_NAME = "KEY_DEFAULT_BACKGROUND_NAME";
    static final String KEY_DEFAULT_PROJECT_NAME = "KEY_DEFAULT_PROJECT_NAME";
    static final String KEY_DEFAULT_SPRITE_NAME = "KEY_DEFAULT_SPRITE_NAME";

    private RunCukes() {
    }

    static void put(String key, Object value) {
        mGlobalState.put(key, value);
    }

    static Object get(String key) {
        return mGlobalState.get(key);
    }
}
