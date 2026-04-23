/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.ui.settingsfragments;

import android.content.SharedPreferences;
import android.content.res.Resources;

import org.catrobat.catroid.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import androidx.annotation.StringDef;

public class AccessibilityProfile {
	private static final String CUSTOM_ACCESSIBILITY_PROFILE = "custom_accessibility_profile";

	@Retention(RetentionPolicy.SOURCE)
	@StringDef({SANS_SERIF, SERIF, DYSLEXIC, LARGE_TEXT, HIGH_CONTRAST, ICONS, LARGE_ICONS, ICON_HIGH_CONTRAST,
			ELEMENT_SPACING, BEGINNER_BRICKS, DRAGNDROP_DELAY})
	@interface AccessibilityFlags {}
	static final String SANS_SERIF = "sans_serif";
	static final String SERIF = "serif";
	static final String DYSLEXIC = "dyslexic";
	static final String LARGE_TEXT = "accessibility_large_text";
	static final String HIGH_CONTRAST = "accessibility_high_contrast";
	static final String ICONS = "accessibility_category_icons";
	static final String LARGE_ICONS = "accessibility_category_icons_big";
	static final String ICON_HIGH_CONTRAST = "accessibility_category_icons_high_contrast";
	static final String ELEMENT_SPACING = "accessibility_element_spacing";
	public static final String BEGINNER_BRICKS = "accessibility_beginner_bricks";
	public static final String DRAGNDROP_DELAY = "accessibility_dragndrop_delay";

	private static final Set<String> BOOLEAN_PREFERENCES;
	static {
		BOOLEAN_PREFERENCES = new HashSet<>(Arrays.asList(LARGE_TEXT, HIGH_CONTRAST, ICONS,
				LARGE_ICONS,
				ICON_HIGH_CONTRAST, ELEMENT_SPACING, BEGINNER_BRICKS, DRAGNDROP_DELAY));
	}

	private static final String FONT_STYLE = "accessibility_font_style";
	private final Set<String> setPreferences = new HashSet<>();

	private AccessibilityProfile(Collection<String> preferences) {
		if (preferences != null) {
			setPreferences.addAll(preferences);
		}
	}

	AccessibilityProfile(String... preferences) {
		this(Arrays.asList(preferences));
	}

	public static AccessibilityProfile fromCurrentPreferences(SharedPreferences sharedPreferences) {
		Set<String> preferences = new HashSet<>();
		for (String preference : BOOLEAN_PREFERENCES) {
			if (sharedPreferences.getBoolean(preference, false)) {
				preferences.add(preference);
			}
		}
		String fontStyle = sharedPreferences.getString(FONT_STYLE, SANS_SERIF);
		preferences.add(fontStyle);
		return new AccessibilityProfile(preferences);
	}

	public static AccessibilityProfile fromCustomProfile(SharedPreferences sharedPreferences) {
		return new AccessibilityProfile(sharedPreferences.getStringSet(CUSTOM_ACCESSIBILITY_PROFILE, null));
	}

	void saveAsCustomProfile(SharedPreferences sharedPreferences) {
		sharedPreferences.edit()
				.putStringSet(CUSTOM_ACCESSIBILITY_PROFILE, setPreferences)
				.apply();
	}

	private void clearCurrent(SharedPreferences sharedPreferences) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		for (String preference : BOOLEAN_PREFERENCES) {
			editor.putBoolean(preference, false);
		}
		editor.putString(FONT_STYLE, SANS_SERIF);
		editor.apply();
	}

	public void setAsCurrent(SharedPreferences sharedPreferences) {
		clearCurrent(sharedPreferences);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		for (@AccessibilityFlags String preference : setPreferences) {
			if (preference.equals(SERIF)
					|| preference.equals(DYSLEXIC)
					|| preference.equals(SANS_SERIF)) {
				editor.putString(FONT_STYLE, preference);
			} else {
				editor.putBoolean(preference, true);
			}
		}
		editor.apply();
	}

	public void applyAccessibilityStyles(Resources.Theme theme) {
		if (setPreferences.contains(LARGE_TEXT)) {
			theme.applyStyle(R.style.FontSizeLarge, true);
		}
		if (setPreferences.contains(HIGH_CONTRAST)) {
			theme.applyStyle(R.style.ContrastHigh, true);
		}
		if (setPreferences.contains(ELEMENT_SPACING)) {
			theme.applyStyle(R.style.SpacingLarge, true);
		}
		if (setPreferences.contains(ICON_HIGH_CONTRAST)) {
			theme.applyStyle(R.style.CategoryIconContrastHigh, true);
		}
		if (setPreferences.contains(ICONS)) {
			theme.applyStyle(R.style.CategoryIconVisible, true);
		}
		if (setPreferences.contains(LARGE_ICONS)) {
			theme.applyStyle(R.style.CategoryIconSizeLarge, true);
		}
		if (setPreferences.contains(SANS_SERIF)) {
			theme.applyStyle(R.style.FontSansSerif, true);
		}
		if (setPreferences.contains(SERIF)) {
			theme.applyStyle(R.style.FontSerif, true);
		}
		if (setPreferences.contains(DYSLEXIC)) {
			theme.applyStyle(R.style.FontDyslexic, true);
		}
	}
}
