/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.cucumber;

import cucumber.api.CucumberOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// Here you can configure which feature or feature-subfolder to test.
@CucumberOptions(features = "features/bricks",
				 format = {"pretty", "html:/sdcard/cucumber/report"})
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
	private static final Map<String, Object> GLOBAL_STATE = Collections.synchronizedMap(new HashMap<String, Object>());

	private Cucumber() {
	}

	public static void put(String key, Object value) {
		GLOBAL_STATE.put(key, value);
	}

	public static Object get(String key) {
		return GLOBAL_STATE.get(key);
	}
}
