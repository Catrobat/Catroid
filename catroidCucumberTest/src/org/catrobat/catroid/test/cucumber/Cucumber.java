package org.catrobat.catroid.test.cucumber;

import cucumber.api.CucumberOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// Here you can configure which feature or feature-subfolder to test.
@CucumberOptions(features = "features/bricks/RepeatBrickDelay.feature")
public final class Cucumber {
	public static final String KEY_SOLO = "KEY_SOLO";
	public static final String KEY_PROJECT = "KEY_PROJECT";
	public static final String KEY_DEFAULT_BACKGROUND_NAME = "KEY_DEFAULT_BACKGROUND_NAME";
	public static final String KEY_DEFAULT_PROJECT_NAME = "KEY_DEFAULT_PROJECT_NAME";
	public static final String KEY_DEFAULT_SPRITE_NAME = "KEY_DEFAULT_SPRITE_NAME";
	public static final String KEY_CURRENT_OBJECT = "KEY_CURRENT_OBJECT";
	public static final String KEY_CURRENT_SCRIPT = "KEY_CURRENT_SCRIPT";
	public static final String KEY_START_TIME_NANO = "KEY_START_TIME_NANO";
	public static final String KEY_STOP_TIME_NANO = "KEY_STOP_TIME_NANO";
	public static final String KEY_LOOP_BEGIN_BRICK = "KEY_LOOP_BEGIN_BRICK";
	// The global state allows glue-class objects to share values with each other.
	private static final Map<String, Object> mGlobalState = Collections.synchronizedMap(new HashMap<String, Object>());

	private Cucumber() {
	}

	public static void put(String key, Object value) {
		mGlobalState.put(key, value);
	}

	public static Object get(String key) {
		return mGlobalState.get(key);
	}
}
