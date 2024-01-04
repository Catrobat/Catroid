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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.DisplayMetrics;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.DroneConfigPreference;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.sync.ProjectsCategoriesSync;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.recyclerview.dialog.AppStoreDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.AppStoreDialogFragment.Companion.Extension;
import org.catrobat.catroid.utils.SnackbarUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import static org.catrobat.catroid.CatroidApplication.defaultSystemLanguage;
import static org.catrobat.catroid.common.SharedPreferenceKeys.DEVICE_LANGUAGE;
import static org.catrobat.catroid.common.SharedPreferenceKeys.LANGUAGE_TAGS;
import static org.catrobat.catroid.common.SharedPreferenceKeys.LANGUAGE_TAG_KEY;
import static org.koin.java.KoinJavaComponent.inject;

public class SettingsFragment extends PreferenceFragment {

	public static final String SETTINGS_MINDSTORMS_NXT_BRICKS_PREFERENCE = "settings_mindstorms_nxt_bricks_preference";
	public static final String SETTINGS_MINDSTORMS_NXT_BRICKS_CHECKBOX_PREFERENCE = "settings_mindstorms_nxt_bricks_checkbox_preference";
	public static final String SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED = "settings_mindstorms_nxt_show_sensor_info_box_disabled";
	public static final String SETTINGS_MINDSTORMS_EV3_BRICKS_PREFERENCE = "settings_mindstorms_ev3_bricks_preference";
	public static final String SETTINGS_MINDSTORMS_EV3_BRICKS_CHECKBOX_PREFERENCE = "settings_mindstorms_ev3_bricks_checkbox_preference";
	public static final String SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED = "settings_mindstorms_ev3_show_sensor_info_box_disabled";
	public static final String SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS = "setting_parrot_ar_drone_bricks";
	public static final String SETTINGS_EDIT_TRUSTED_DOMAINS = "setting_trusted_domains";
	public static final String SETTINGS_SHOW_JUMPING_SUMO_BRICKS = "setting_parrot_jumping_sumo_bricks";
	public static final String SETTINGS_SHOW_EMBROIDERY_BRICKS_PREFERENCE = "setting_embroidery_bricks_preference";
	public static final String SETTINGS_SHOW_EMBROIDERY_BRICKS_CHECKBOX_PREFERENCE = "setting_embroidery_bricks_checkbox_preference";
	public static final String SETTINGS_SHOW_PHIRO_BRICKS_CHECKBOX_PREFERENCE = "setting_enable_phiro_bricks_checkbox_preference";
	public static final String SETTINGS_SHOW_PHIRO_BRICKS_PREFERENCE = "setting_enable_phiro_bricks_preference";
	public static final String SETTINGS_SHOW_ARDUINO_BRICKS = "setting_arduino_bricks";
	public static final String SETTINGS_SHOW_RASPI_BRICKS = "setting_raspi_bricks";
	public static final String SETTINGS_SHOW_NFC_BRICKS = "setting_nfc_bricks";
	public static final String SETTINGS_PARROT_AR_DRONE_CATROBAT_TERMS_OF_SERVICE_ACCEPTED_PERMANENTLY = "setting_parrot_ar_drone_catrobat_terms_of_service_accepted_permanently";
	public static final String SETTINGS_CAST_GLOBALLY_ENABLED = "setting_cast_globally_enabled";
	public static final String SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS = "setting_ai_speech_recognition";
	public static final String SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS = "setting_ai_speech_synthetization";
	public static final String SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS = "setting_ai_face_detection";
	public static final String SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS = "setting_ai_pose_detection";
	public static final String SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS = "setting_ai_text_recognition";
	public static final String SETTINGS_SHOW_AI_OBJECT_DETECTION_SENSORS = "setting_ai_object_detection";

	public static final String SETTINGS_MULTIPLAYER_VARIABLES_ENABLED = "setting_multiplayer_variables_enabled";
	public static final String SETTINGS_SHOW_HINTS = "setting_enable_hints";
	public static final String SETTINGS_MULTILINGUAL = "setting_multilingual";
	public static final String SETTINGS_PARROT_JUMPING_SUMO_CATROBAT_TERMS_OF_SERVICE_ACCEPTED_PERMANENTLY =
			"setting_parrot_jumping_sumo_catrobat_terms_of_service_accepted_permanently";
	public static final String SETTINGS_TEST_BRICKS = "setting_test_bricks";
	PreferenceScreen screen = null;

	public static final String AI_SENSORS_SCREEN_KEY = "setting_ai_screen";
	public static final String ACCESSIBILITY_SCREEN_KEY = "setting_accessibility_screen";
	public static final String NXT_SCREEN_KEY = "setting_nxt_screen";
	public static final String EV3_SCREEN_KEY = "setting_ev3_screen";
	public static final String DRONE_SCREEN_KEY = "settings_drone_screen";
	public static final String RASPBERRY_SCREEN_KEY = "settings_raspberry_screen";

	public static final String NXT_SETTINGS_CATEGORY = "setting_nxt_category";
	public static final String[] NXT_SENSORS = {"setting_mindstorms_nxt_sensor_1", "setting_mindstorms_nxt_sensor_2",
			"setting_mindstorms_nxt_sensor_3", "setting_mindstorms_nxt_sensor_4"};

	public static final String EV3_SETTINGS_CATEGORY = "setting_ev3_category";
	public static final String[] EV3_SENSORS = {"setting_mindstorms_ev3_sensor_1", "setting_mindstorms_ev3_sensor_2",
			"setting_mindstorms_ev3_sensor_3", "setting_mindstorms_ev3_sensor_4"};

	public static final String DRONE_SETTINGS_CATEGORY = "setting_drone_category";
	public static final String DRONE_CONFIGS = "setting_drone_basic_configs";
	public static final String DRONE_ALTITUDE_LIMIT = "setting_drone_altitude_limit";
	public static final String DRONE_VERTICAL_SPEED = "setting_drone_vertical_speed";
	public static final String DRONE_ROTATION_SPEED = "setting_drone_rotation_speed";
	public static final String DRONE_TILT_ANGLE = "setting_drone_tilt_angle";

	public static final String RASPI_CONNECTION_SETTINGS_CATEGORY = "setting_raspi_connection_settings_category";
	public static final String RASPI_HOST = "setting_raspi_host_preference";
	public static final String RASPI_PORT = "setting_raspi_port_preference";
	public static final String RASPI_VERSION_SPINNER = "setting_raspi_version_preference";

	public static final String SETTINGS_CRASH_REPORTS = "setting_enable_crash_reports";
	public static final String TAG = SettingsFragment.class.getSimpleName();

	public static final String SETTINGS_USE_CATBLOCKS = "settings_use_catblocks";

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setToChosenLanguage(getActivity());

		addPreferencesFromResource(R.xml.preferences);
		setHintPreferences();
		setLanguage();

		screen = getPreferenceScreen();

		if (!BuildConfig.FEATURE_EMBROIDERY_ENABLED) {
			CheckBoxPreference embroideryPreference =
					(CheckBoxPreference) findPreference(SETTINGS_SHOW_EMBROIDERY_BRICKS_CHECKBOX_PREFERENCE);
			embroideryPreference.setEnabled(false);
			screen.removePreference(embroideryPreference);
		}

		if (!BuildConfig.FEATURE_PHIRO_ENABLED) {
			CheckBoxPreference phiroPreference =
					(CheckBoxPreference) findPreference(SETTINGS_SHOW_PHIRO_BRICKS_CHECKBOX_PREFERENCE);
			phiroPreference.setEnabled(false);
			screen.removePreference(phiroPreference);
		}

		if (!BuildConfig.FEATURE_PARROT_JUMPING_SUMO_ENABLED) {
			CheckBoxPreference jumpingSumoPreference = (CheckBoxPreference) findPreference(SETTINGS_SHOW_JUMPING_SUMO_BRICKS);
			jumpingSumoPreference.setEnabled(false);
			screen.removePreference(jumpingSumoPreference);
		}

		if (!BuildConfig.FEATURE_ARDUINO_ENABLED) {
			CheckBoxPreference arduinoPreference = (CheckBoxPreference) findPreference(SETTINGS_SHOW_ARDUINO_BRICKS);
			arduinoPreference.setEnabled(false);
			screen.removePreference(arduinoPreference);
		}

		if (!BuildConfig.FEATURE_RASPI_ENABLED) {
			CheckBoxPreference raspiPreference = (CheckBoxPreference) findPreference(SETTINGS_SHOW_RASPI_BRICKS);
			raspiPreference.setEnabled(false);
			screen.removePreference(raspiPreference);
		}

		if (!BuildConfig.FEATURE_CAST_ENABLED) {
			CheckBoxPreference globalCastPreference = (CheckBoxPreference) findPreference(SETTINGS_CAST_GLOBALLY_ENABLED);
			globalCastPreference.setEnabled(false);
			screen.removePreference(globalCastPreference);
		}

		if (!BuildConfig.FEATURE_NFC_ENABLED) {
			CheckBoxPreference nfcPreference = (CheckBoxPreference) findPreference(SETTINGS_SHOW_NFC_BRICKS);
			nfcPreference.setEnabled(false);
			screen.removePreference(nfcPreference);
		}

		if (!BuildConfig.FEATURE_MULTIPLAYER_VARIABLES_ENABLED) {
			CheckBoxPreference multiplayerPreference =
					(CheckBoxPreference) findPreference(SETTINGS_MULTIPLAYER_VARIABLES_ENABLED);
			multiplayerPreference.setEnabled(false);
			screen.removePreference(multiplayerPreference);
		}

		if (!BuildConfig.FEATURE_TESTBRICK_ENABLED) {
			CheckBoxPreference testPreference =
					(CheckBoxPreference) findPreference(SETTINGS_TEST_BRICKS);
			testPreference.setEnabled(BuildConfig.DEBUG);
			screen.removePreference(testPreference);
		}

		setCorrectPreferenceViewForEmbroidery();
		setCorrectPreferenceViewForPhiro();
	}

	@Override
	public void onResume() {
		super.onResume();
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.preference_title);
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		String key = preference.getKey();
		switch (key) {
			case AI_SENSORS_SCREEN_KEY:
				getFragmentManager().beginTransaction()
						.replace(R.id.content_frame, new AISettingsFragment(),
								AISettingsFragment.Companion.getTAG())
						.addToBackStack(AISettingsFragment.Companion.getTAG())
						.commit();
				break;
			case ACCESSIBILITY_SCREEN_KEY:
				getFragmentManager().beginTransaction()
						.replace(R.id.content_frame, new AccessibilitySettingsFragment(), AccessibilitySettingsFragment.TAG)
						.addToBackStack(AccessibilitySettingsFragment.TAG)
						.commit();
				break;
			case NXT_SCREEN_KEY:
				getFragmentManager().beginTransaction()
						.replace(R.id.content_frame, new NXTSensorsSettingsFragment(), NXTSensorsSettingsFragment.TAG)
						.addToBackStack(NXTSensorsSettingsFragment.TAG)
						.commit();
				break;
			case EV3_SCREEN_KEY:
				getFragmentManager().beginTransaction()
						.replace(R.id.content_frame, new Ev3SensorsSettingsFragment(), Ev3SensorsSettingsFragment.TAG)
						.addToBackStack(Ev3SensorsSettingsFragment.TAG)
						.commit();
				break;
			case DRONE_SCREEN_KEY:
				getFragmentManager().beginTransaction()
						.replace(R.id.content_frame, new ParrotARDroneSettingsFragment(),
								ParrotARDroneSettingsFragment.TAG)
						.addToBackStack(ParrotARDroneSettingsFragment.TAG)
						.commit();
				break;
			case RASPBERRY_SCREEN_KEY:
				getFragmentManager().beginTransaction()
						.replace(R.id.content_frame, new RaspberryPiSettingsFragment(), RaspberryPiSettingsFragment.TAG)
						.addToBackStack(RaspberryPiSettingsFragment.TAG)
						.commit();
				break;
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	@SuppressWarnings("deprecation")
	private void setHintPreferences() {
		CheckBoxPreference hintCheckBoxPreference = (CheckBoxPreference) findPreference(SETTINGS_SHOW_HINTS);
		hintCheckBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.getEditor().remove(SnackbarUtil.SHOWN_HINT_LIST).commit();
				return true;
			}
		});
	}

	public static boolean isEmroiderySharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_SHOW_EMBROIDERY_BRICKS_CHECKBOX_PREFERENCE, context);
	}

	public static boolean isDroneSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS, context);
	}

	public static boolean isJSSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_SHOW_JUMPING_SUMO_BRICKS, context);
	}

	public static boolean isMindstormsNXTSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_MINDSTORMS_NXT_BRICKS_CHECKBOX_PREFERENCE, context);
	}

	public static boolean isMindstormsEV3SharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_MINDSTORMS_EV3_BRICKS_CHECKBOX_PREFERENCE, context);
	}

	public static boolean isPhiroSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_SHOW_PHIRO_BRICKS_CHECKBOX_PREFERENCE,
				context);
	}

	public static boolean isCastSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_CAST_GLOBALLY_ENABLED, context);
	}

	public static void setPhiroSharedPreferenceEnabled(Context context, boolean value) {
		getSharedPreferences(context).edit()
				.putBoolean(SETTINGS_SHOW_PHIRO_BRICKS_CHECKBOX_PREFERENCE, value)
				.apply();
	}

	public static void setJumpingSumoSharedPreferenceEnabled(Context context, boolean value) {
		getSharedPreferences(context).edit()
				.putBoolean(SETTINGS_SHOW_JUMPING_SUMO_BRICKS, value)
				.apply();
	}

	public static void setArduinoSharedPreferenceEnabled(Context context, boolean value) {
		getSharedPreferences(context).edit()
				.putBoolean(SETTINGS_SHOW_ARDUINO_BRICKS, value)
				.apply();
	}

	public static void setRaspiSharedPreferenceEnabled(Context context, boolean value) {
		getSharedPreferences(context).edit()
				.putBoolean(SETTINGS_SHOW_RASPI_BRICKS, value)
				.apply();
	}

	public static boolean isAISpeechRecognitionSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS, context);
	}

	public static void setAISpeechReconitionPreferenceEnabled(Context context, boolean value) {
		getSharedPreferences(context).edit()
				.putBoolean(SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS, value)
				.apply();
	}

	public static boolean isAIFaceDetectionSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS, context);
	}

	public static void setAIFaceDetectionPreferenceEnabled(Context context, boolean value) {
		getSharedPreferences(context).edit()
				.putBoolean(SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS, value)
				.apply();
	}

	public static boolean isAIPoseDetectionSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS, context);
	}

	public static void setAIPoseDetectionPreferenceEnabled(Context context, boolean value) {
		getSharedPreferences(context).edit()
				.putBoolean(SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS, value)
				.apply();
	}

	public static boolean isAISpeechSynthetizationSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS, context);
	}

	public static void setAISpeechSynthetizationPreferenceEnabled(Context context, boolean value) {
		getSharedPreferences(context).edit()
				.putBoolean(SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS, value)
				.apply();
	}

	public static boolean isAITextRecognitionSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS, context);
	}

	public static void setAITextRecognitionPreferenceEnabled(Context context, boolean value) {
		getSharedPreferences(context).edit()
				.putBoolean(SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS, value)
				.apply();
	}

	public static boolean isAIObjectDetectionSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_SHOW_AI_OBJECT_DETECTION_SENSORS, context);
	}

	public static void setAIObjectDetectionPreferenceEnabled(Context context, boolean value) {
		getSharedPreferences(context).edit()
				.putBoolean(SETTINGS_SHOW_AI_OBJECT_DETECTION_SENSORS, value)
				.apply();
	}

	public static boolean isArduinoSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_SHOW_ARDUINO_BRICKS, context);
	}

	public static boolean isRaspiSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_SHOW_RASPI_BRICKS, context);
	}

	public static boolean isNfcSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_SHOW_NFC_BRICKS, context);
	}

	public static boolean isTestSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(BuildConfig.DEBUG, SETTINGS_TEST_BRICKS, context);
	}

	private static boolean getBooleanSharedPreference(boolean defaultValue, String settingsString, Context context) {
		return getSharedPreferences(context).getBoolean(settingsString, defaultValue);
	}

	private static SharedPreferences getSharedPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	public static NXTSensor.Sensor[] getLegoNXTSensorMapping(Context context) {
		NXTSensor.Sensor[] sensorMapping = new NXTSensor.Sensor[4];
		for (int i = 0; i < 4; i++) {
			String sensor = getSharedPreferences(context).getString(NXT_SENSORS[i], null);
			sensorMapping[i] = NXTSensor.Sensor.getSensorFromSensorCode(sensor);
		}

		return sensorMapping;
	}

	public static EV3Sensor.Sensor[] getLegoEV3SensorMapping(Context context) {
		EV3Sensor.Sensor[] sensorMapping = new EV3Sensor.Sensor[4];
		for (int i = 0; i < 4; i++) {
			String sensor = getSharedPreferences(context).getString(EV3_SENSORS[i], null);
			sensorMapping[i] = EV3Sensor.Sensor.getSensorFromSensorCode(sensor);
		}

		return sensorMapping;
	}

	public static String getRaspiHost(Context context) {
		return getSharedPreferences(context).getString(RASPI_HOST, null);
	}

	public static int getRaspiPort(Context context) {
		return Integer.parseInt(getSharedPreferences(context).getString(RASPI_PORT, null));
	}

	public static String getRaspiRevision(Context context) {
		return getSharedPreferences(context).getString(RASPI_VERSION_SPINNER, null);
	}

	public static void setLegoMindstormsNXTSensorMapping(Context context, NXTSensor.Sensor[] sensorMapping) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		for (int i = 0; i < NXT_SENSORS.length; i++) {
			editor.putString(NXT_SENSORS[i], sensorMapping[i].getSensorCode());
		}

		editor.apply();
	}

	public static void setLegoMindstormsEV3SensorMapping(Context context, EV3Sensor.Sensor[] sensorMapping) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		for (int i = 0; i < EV3_SENSORS.length; i++) {
			editor.putString(EV3_SENSORS[i], sensorMapping[i].getSensorCode());
		}

		editor.apply();
	}

	public static void setLegoMindstormsNXTSensorMapping(Context context, NXTSensor.Sensor sensor, String sensorSetting) {
		getSharedPreferences(context).edit()
				.putString(sensorSetting, sensor.getSensorCode())
				.apply();
	}

	public static void setLegoMindstormsEV3SensorMapping(Context context, EV3Sensor.Sensor sensor, String sensorSetting) {
		getSharedPreferences(context).edit()
				.putString(sensorSetting, sensor.getSensorCode())
				.apply();
	}

	public static DroneConfigPreference.Preferences[] getDronePreferenceMapping(Context context) {

		final String[] dronePreferences =
				new String[] {DRONE_CONFIGS, DRONE_ALTITUDE_LIMIT, DRONE_VERTICAL_SPEED, DRONE_ROTATION_SPEED, DRONE_TILT_ANGLE};

		DroneConfigPreference.Preferences[] preferenceMapping = new DroneConfigPreference.Preferences[5];
		for (int i = 0; i < 5; i++) {
			String preference = getSharedPreferences(context).getString(dronePreferences[i], null);
			preferenceMapping[i] = DroneConfigPreference.Preferences.getPreferenceFromPreferenceCode(preference);
		}

		return preferenceMapping;
	}

	public static DroneConfigPreference.Preferences getDronePreferenceMapping(Context context, String
			preferenceSetting) {
		String preference = getSharedPreferences(context).getString(preferenceSetting, null);
		return DroneConfigPreference.Preferences.getPreferenceFromPreferenceCode(preference);
	}

	public static void enableLegoMindstormsNXTBricks(Context context) {
		getSharedPreferences(context).edit()
				.putBoolean(SETTINGS_MINDSTORMS_NXT_BRICKS_CHECKBOX_PREFERENCE, true)
				.apply();
	}

	public static void enableLegoMindstormsEV3Bricks(Context context) {
		getSharedPreferences(context).edit()
				.putBoolean(SETTINGS_MINDSTORMS_EV3_BRICKS_CHECKBOX_PREFERENCE, true)
				.apply();
	}

	@VisibleForTesting
	public static void setMultiplayerVariablesPreferenceEnabled(Context context, boolean value) {
		getSharedPreferences(context).edit()
				.putBoolean(SETTINGS_MULTIPLAYER_VARIABLES_ENABLED, value)
				.apply();
	}

	public static boolean isMultiplayerVariablesPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_MULTIPLAYER_VARIABLES_ENABLED, context)
				|| ProjectManager.getInstance().getCurrentProject().hasMultiplayerVariables();
	}

	private void setLanguage() {
		final List<String> languagesNames = new ArrayList<>();
		for (String languageTag : LANGUAGE_TAGS) {
			if (DEVICE_LANGUAGE.equals(languageTag)) {
				languagesNames.add(getResources().getString(R.string.device_language));
			} else {
				Locale mLocale = Locale.forLanguageTag(languageTag);
				languagesNames.add(mLocale.getDisplayName(mLocale));
			}
		}

		String[] languages = new String[languagesNames.size()];
		languagesNames.toArray(languages);

		final ListPreference listPreference = (ListPreference) findPreference(SETTINGS_MULTILINGUAL);
		listPreference.setEntries(languages);
		listPreference.setEntryValues(LANGUAGE_TAGS);
		listPreference.setOnPreferenceChangeListener((preference, languageTag) -> {
			String selectedLanguageTag = languageTag.toString();
			setLanguageSharedPreference(getActivity().getBaseContext(), selectedLanguageTag);
			startActivity(new Intent(getActivity().getBaseContext(), MainMenuActivity.class));
			getActivity().finishAffinity();
			new Thread(() -> inject(ProjectsCategoriesSync.class).getValue().sync(true));
			return true;
		});
	}

	public static void setToChosenLanguage(Activity activity) {
		SharedPreferences sharedPreferences = getSharedPreferences(activity.getApplicationContext());
		String languageTag = sharedPreferences.getString(LANGUAGE_TAG_KEY, "");
		Locale mLocale;
		if (languageTag.equals(DEVICE_LANGUAGE)) {
			mLocale = Locale.forLanguageTag(defaultSystemLanguage);
		} else {
			mLocale = Arrays.asList(LANGUAGE_TAGS).contains(languageTag)
					? Locale.forLanguageTag(languageTag)
					: Locale.forLanguageTag(defaultSystemLanguage);
		}

		Locale.setDefault(mLocale);
		updateLocale(activity, mLocale);
		updateLocale(activity.getApplicationContext(), mLocale);
		SensorHandler.setUserLocaleTag(mLocale.toLanguageTag());
	}

	public static void updateLocale(Context context, Locale locale) {
		Resources resources = context.getResources();
		DisplayMetrics displayMetrics = resources.getDisplayMetrics();
		Configuration conf = resources.getConfiguration();
		conf.setLocale(locale);
		resources.updateConfiguration(conf, displayMetrics);
	}

	public static void setLanguageSharedPreference(Context context, String value) {
		getSharedPreferences(context).edit()
				.putString(LANGUAGE_TAG_KEY, value)
				.apply();
	}

	public static void removeLanguageSharedPreference(Context context) {
		getSharedPreferences(context).edit()
				.remove(LANGUAGE_TAG_KEY)
				.apply();
	}

	public static boolean useCatBlocks(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_USE_CATBLOCKS, context);
	}

	public static void setUseCatBlocks(Context context, boolean useCatBlocks) {
		getSharedPreferences(context).edit()
				.putBoolean(SETTINGS_USE_CATBLOCKS, useCatBlocks)
				.apply();
	}

	private void setCorrectPreferenceViewForEmbroidery() {
		CheckBoxPreference embroideryCheckBoxPreference =
				(CheckBoxPreference) findPreference(SettingsFragment.SETTINGS_SHOW_EMBROIDERY_BRICKS_CHECKBOX_PREFERENCE);
		Preference simplePreferenceField = findPreference(SettingsFragment.SETTINGS_SHOW_EMBROIDERY_BRICKS_PREFERENCE);

		if (BuildConfig.FLAVOR != Constants.FLAVOR_EMBROIDERY_DESIGNER) {
			getPreferenceScreen().removePreference(embroideryCheckBoxPreference);

			simplePreferenceField.setOnPreferenceClickListener(preference -> {
				AppStoreDialogFragment.newInstance(Extension.EMBROIDERY).show(((FragmentActivity) getActivity()).getSupportFragmentManager(),
						AppStoreDialogFragment.Companion.getTAG());
				return true;
			});
		} else {
			getPreferenceScreen().removePreference(simplePreferenceField);
		}
	}

	private void setCorrectPreferenceViewForPhiro() {
		CheckBoxPreference phiroCheckBoxPreference =
				(CheckBoxPreference) findPreference(SettingsFragment.SETTINGS_SHOW_PHIRO_BRICKS_CHECKBOX_PREFERENCE);
		Preference simplePreferenceField = findPreference(SettingsFragment.SETTINGS_SHOW_PHIRO_BRICKS_PREFERENCE);

		if (BuildConfig.FLAVOR != Constants.FLAVOR_PHIRO) {
			getPreferenceScreen().removePreference(phiroCheckBoxPreference);

			simplePreferenceField.setOnPreferenceClickListener(preference -> {
				AppStoreDialogFragment.newInstance(Extension.PHIRO).show(((FragmentActivity) getActivity()).getSupportFragmentManager(),
						AppStoreDialogFragment.Companion.getTAG());
				return true;
			});
		} else {
			getPreferenceScreen().removePreference(simplePreferenceField);
		}
	}
}
