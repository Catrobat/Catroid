/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.common;

public final class SharedPreferenceKeys {

	private SharedPreferenceKeys() {
		throw new AssertionError("No.");
	}

	public static final String ACCESSIBILITY_PROFILE_PREFERENCE_KEY = "AccessibilityProfile";
	public static final String AGREED_TO_PRIVACY_POLICY_PREFERENCE_KEY = "AgreedToPrivacyPolicy";
	public static final String SHOW_COPY_PROJECTS_FROM_EXTERNAL_STORAGE_DIALOG = "ShowCopyProjectsToInternalStorage";

	public static final String DEVICE_LANGUAGE = "deviceLanguage";
	public static final String LANGUAGE_TAG_KEY = "applicationLanguage";
	public static final String[] LANGUAGE_CODE = {DEVICE_LANGUAGE, "az", "in", "bs", "ca", "cs", "sr-rCS",
			"sr-rSP", "da", "de", "en-rAU", "en-rCA", "en-rGB", "en", "es", "el", "fr", "gl", "hr", "it",
			"sw", "hu", "mk", "ms", "nl", "no", "pl", "pt-rBR", "pt", "ru", "ro", "sq", "sl", "sk",
			"sv", "vi", "tr", "uk", "bg", "ml", "ta", "kn", "te", "th", "gu", "hi", "ja", "ko", "lt", "zh-rCN",
			"zh-rTW", "ar", "ur", "fa", "ps", "sd", "iw"};

	public static final String SHOW_DETAILS_LOOKS_PREFERENCE_KEY = "showDetailsLookList";
	public static final String SHOW_DETAILS_PROJECTS_PREFERENCE_KEY = "showDetailsProjectList";
	public static final String SHOW_DETAILS_SCENES_PREFERENCE_KEY = "showDetailsSceneList";
	public static final String SHOW_DETAILS_SCRATCH_PROJECTS_PREFERENCE_KEY = "showDetailsScratchProjects";
	public static final String SHOW_DETAILS_SOUNDS_PREFERENCE_KEY = "showDetailsSoundList";
	public static final String SHOW_DETAILS_SPRITES_PREFERENCE_KEY = "showDetailsSpriteList";

	public static final String SCRATCH_CONVERTER_CLIENT_ID_PREFERENCE_KEY = "scratchconverter.clientID";
	public static final String SCRATCH_CONVERTER_DOWNLOAD_STATE_PREFERENCE_KEY = "scratchconverter"
			+ ".downloadStatePref";
}
