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
package org.catrobat.catroid.common;

public final class SharedPreferenceKeys {

	private SharedPreferenceKeys() {
		throw new AssertionError("No.");
	}

	public static final String ACCESSIBILITY_PROFILE_PREFERENCE_KEY = "AccessibilityProfile";
	public static final String AGREED_TO_PRIVACY_POLICY_VERSION =
			"AgreedToCurrentPrivacyPolicy";

	public static final String DEVICE_LANGUAGE = "deviceLanguage";
	public static final String LANGUAGE_TAG_KEY = "applicationLanguage";
	public static final String[] LANGUAGE_TAGS = {DEVICE_LANGUAGE, "af", "az", "ms", "bs", "ca",
			"da", "de", "en-AU", "en-CA", "en-GB", "en", "es", "fr", "gl", "ha", "hr", "ig", "id",
			"it", "sw", "lt", "hu", "nl", "no", "pl", "pt-BR", "pt", "ro", "sq", "sk", "sl", "fi",
			"sv", "vi", "tw", "tr", "cs", "el", "bg", "mk", "ru", "sr-CS", "sr", "sr-SP", "uk",
			"kk", "he", "ur", "ar", "sd", "fa", "ps", "hi", "bn", "gu", "ta", "te", "kn", "ml",
			"si", "th", "zh-CN", "zh-TW", "ja", "ko"};

	public static final String SHOW_DETAILS_LOOKS_PREFERENCE_KEY = "showDetailsLookList";
	public static final String SHOW_DETAILS_PROJECTS_PREFERENCE_KEY = "showDetailsProjectList";
	public static final String SHOW_DETAILS_SCENES_PREFERENCE_KEY = "showDetailsSceneList";
	public static final String SHOW_DETAILS_SCRATCH_PROJECTS_PREFERENCE_KEY = "showDetailsScratchProjects";
	public static final String SHOW_DETAILS_SOUNDS_PREFERENCE_KEY = "showDetailsSoundList";
	public static final String SHOW_DETAILS_SPRITES_PREFERENCE_KEY = "showDetailsSpriteList";

	public static final String SORT_PROJECTS_PREFERENCE_KEY = "sortProjectsList";
	public static final String SORT_VARIABLE_PREFERENCE_KEY = "sortVariableList";

	public static final String INDEXING_VARIABLE_PREFERENCE_KEY = "variableIndexing";

	public static final String DISABLE_HINTS_DIALOG_SHOWN_PREFERENCE_KEY =
			"disableHintsDialogShown";

	public static final String SCRATCH_CONVERTER_CLIENT_ID_PREFERENCE_KEY = "scratchconverter.clientID";
	public static final String SCRATCH_CONVERTER_DOWNLOAD_STATE_PREFERENCE_KEY = "scratchconverter"
			+ ".downloadStatePref";

	public static final String TIME_SPENT_IN_APP_IN_SECONDS_KEY = "timeSpentInApp";
	public static final String LAST_USED_DATE_KEY = "lastUsedDate";
	public static final String SHOW_SURVEY_KEY = "showSurvey";
	public static final String SURVEY_URL1_HASH_KEY = "surveyUrl1Hash";
	public static final String SURVEY_URL2_HASH_KEY = "surveyUrl2Hash";
	public static final String NEW_SPRITE_VISUAL_PLACEMENT_KEY = "newSpriteVisualPlacement";
	public static final String SHOW_MULTIPLAYER_BLUETOOTH_DIALOG_KEY = "showMultiplayerBluetoothDialog";
}
