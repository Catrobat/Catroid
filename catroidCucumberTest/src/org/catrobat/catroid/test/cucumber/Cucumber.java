package org.catrobat.catroid.test.cucumber;

import cucumber.api.CucumberOptions;

import java.util.HashMap;
import java.util.Map;

// Here you can configure which feature or feature-subfolder to use.
@CucumberOptions(features = "features/bricks/RepeatBrick.feature")
public final class Cucumber {
	static final String KEY_SOLO = "KEY_SOLO";
	static final String KEY_PROJECT = "KEY_PROJECT";
	static final String KEY_DEFAULT_BACKGROUND_NAME = "KEY_DEFAULT_BACKGROUND_NAME";
	static final String KEY_DEFAULT_PROJECT_NAME = "KEY_DEFAULT_PROJECT_NAME";
	static final String KEY_DEFAULT_SPRITE_NAME = "KEY_DEFAULT_SPRITE_NAME";
	static final String KEY_CURRENT_OBJECT = "KEY_CURRENT_OBJECT";
	static final String KEY_CURRENT_SCRIPT = "KEY_CURRENT_SCRIPT";
	static final String KEY_START_TIME_NANO = "KEY_START_TIME_NANO";
	static final String KEY_STOP_TIME_NANO = "KEY_STOP_TIME_NANO";
	static final String KEY_LOOP_BEGIN_BRICK = "KEY_LOOP_BEGIN_BRICK";
	// The global state allows glue-class objects to share values with each other.
	private static final Map<String, Object> mGlobalState = new HashMap<String, Object>();

	private Cucumber() {
	}

	static void put(String key, Object value) {
		mGlobalState.put(key, value);
	}

	static Object get(String key) {
		return mGlobalState.get(key);
	}
}
