/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.ui;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.adapter.AccessibilityCheckboxAdapter;
import org.catrobat.catroid.ui.adapter.AccessibilityCheckboxAdapter.AccessibilityCheckbox;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.TextSizeUtil;
import org.catrobat.catroid.utils.Utils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AccessibilityPreferencesActivity extends BaseActivity {

	private static final String TAG = AccessibilityPreferencesActivity.class.getSimpleName();

	public static final String XML_PROFILE_TAG = "profile";
	public static final String XML_PARAMETER_TAG = "parameter";
	public static final String XML_NAME_FIELD = "name";
	public static final String XML_VALUE_FIELD = "value";
	private static final int FONTFACE_STANDARD = 0;
	private static final int FONTFACE_SERIF = 1;
	private static final int FONTFACE_DYSLEXIC = 2;
	private int selectedFontface;

	public AccessibilityCheckboxAdapter adapter;
	private boolean changesMade;
	private boolean inSelectedProfile;
	private String selectedProfileName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accessibility_preferences);
		setUpActionBar();
		setReturnByPressingBackButton(true);

		adapter = new AccessibilityCheckboxAdapter(this, R.layout.fragment_access_preference_list_item, createPreferences());

		final ListView listView = (ListView) findViewById(R.id.access_preference_list_view);
		listView.setFocusable(false);
		listView.setAdapter(adapter);

		Intent intent = getIntent();
		int selectedProfileId = intent.getIntExtra(AccessibilityProfilesActivity.PROFILE_ID, -1);
		inSelectedProfile = selectedProfileId != -1;

		if (inSelectedProfile) {
			Button switchProfilesButton = (Button) findViewById(R.id.access_switch_to_predefined_profiles_button);
			View separationLine = findViewById(R.id.access_separation_line_switch_profiles);
			TextView activeProfileTextView = (TextView) findViewById(R.id.access_label_active_profile);

			switchProfilesButton.setVisibility(View.GONE);
			separationLine.setVisibility(View.GONE);
			activeProfileTextView.setText(getResources().getString(R.string.preference_access_selected_profile));

			loadProfile(selectedProfileId);
		} else {
			if (BaseSettingsActivity.getActiveAccessibilityProfile(getApplicationContext()).equals(BaseSettingsActivity.ACCESS_PROFILE_NONE)) {
				BaseSettingsActivity.setActiveAccessibilityProfile(getApplicationContext(), BaseSettingsActivity.ACCESS_PROFILE_STANDARD);
				parseAccessibilityPredefinedProfile(BaseSettingsActivity.ACCESS_PROFILE_STANDARD);
				saveAccessibilityProfilePreference();
				saveAccessibilityMyProfilePreference();
			} else {
				loadAccessibilityProfilePreference();
			}
			selectedProfileName = BaseSettingsActivity.getActiveAccessibilityProfile(getApplicationContext());
		}

		updateAccessibilityActiveProfile();

		final View contentView = findViewById(android.R.id.content);
		listView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				contentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				Utils.setListViewHeightBasedOnItemsAndTheirWidth(listView);
				SnackbarUtil.showHintSnackBar(AccessibilityPreferencesActivity.this, R.string.hint_accessibility);
			}
		});

		TextSizeUtil.enlargeViewGroup((ViewGroup) getWindow().getDecorView().getRootView());
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	public void onBackPressed() {
		if (changesMade) {
			showRestartDialog();
		} else {
			super.onBackPressed();
		}
	}

	private void setUpActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.preference_title_access);
		actionBar.setHomeButtonEnabled(true);
	}

	private List<AccessibilityCheckbox> createPreferences() {
		List<AccessibilityCheckbox> list = new ArrayList<>();

		AccessibilityCheckbox largeText = new AccessibilityCheckbox();
		largeText.title = getResources().getString(R.string.preference_access_title_large_text);
		largeText.summary = getResources().getString(R.string.preference_access_summary_large_text);
		list.add(largeText);

		AccessibilityCheckbox highContrast = new AccessibilityCheckbox();
		highContrast.title = getResources().getString(R.string.preference_access_title_high_contrast);
		highContrast.summary = getResources().getString(R.string.preference_access_summary_high_contrast);
		list.add(highContrast);

		AccessibilityCheckbox additionalIcons = new AccessibilityCheckbox();
		additionalIcons.title = getResources().getString(R.string.preference_access_title_additional_icons);
		additionalIcons.summary = getResources().getString(R.string.preference_access_summary_additional_icons);
		list.add(additionalIcons);

		AccessibilityCheckbox largeIcons = new AccessibilityCheckbox();
		largeIcons.title = getResources().getString(R.string.preference_access_title_large_icons);
		largeIcons.summary = getResources().getString(R.string.preference_access_summary_large_icons);
		list.add(largeIcons);

		AccessibilityCheckbox highContrastIcons = new AccessibilityCheckbox();
		highContrastIcons.title = getResources().getString(R.string.preference_access_title_high_contrast_icons);
		highContrastIcons.summary = getResources().getString(R.string.preference_access_summary_high_contrast_icons);
		list.add(highContrastIcons);

		AccessibilityCheckbox largeElementSpacing = new AccessibilityCheckbox();
		largeElementSpacing.title = getResources().getString(R.string.preference_access_title_largeelementspacing);
		largeElementSpacing.summary = getResources().getString(R.string.preference_access_summary_largeelementspacing);
		list.add(largeElementSpacing);

		AccessibilityCheckbox starterBricks = new AccessibilityCheckbox();
		starterBricks.title = getResources().getString(R.string.preference_access_title_starter_bricks);
		starterBricks.summary = getResources().getString(R.string.preference_access_summary_starter_bricks);
		list.add(starterBricks);

		AccessibilityCheckbox dragndropDelay = new AccessibilityCheckbox();
		dragndropDelay.title = getResources().getString(R.string.preference_access_title_dragndrop_delay);
		dragndropDelay.summary = getResources().getString(R.string.preference_access_summary_dragndrop_delay);
		list.add(dragndropDelay);

		return list;
	}

	public void switchToPredefinedProfiles(View view) {
		Intent intent = new Intent(AccessibilityPreferencesActivity.this, AccessibilityProfilesActivity.class);
		AccessibilityPreferencesActivity.this.startActivity(intent);
	}

	public void checkboxPressed(View view) {
		CheckBox checkBox = (CheckBox) view.findViewById(R.id.access_preference_checkbox);
		checkBox.setChecked(!checkBox.isChecked());

		String title = (String) ((TextView) view.findViewById(R.id.access_preference_title)).getText();
		for (int i = 0; i < adapter.getCount(); i++) {
			AccessibilityCheckbox accessCheckbox = adapter.getItem(i);
			if (accessCheckbox.title.equals(title)) {
				accessCheckbox.value = checkBox.isChecked();
			}
		}
		if (!selectedProfileName.equals(BaseSettingsActivity.ACCESS_PROFILE_MYPROFILE)) {
			showMyProfileCreatedDialog();
			selectedProfileName = BaseSettingsActivity.ACCESS_PROFILE_MYPROFILE;
			updateAccessibilityActiveProfile();
		}
		if (!inSelectedProfile) {
			changesMade = true;
		}
		saveAccessibilityMyProfilePreference();
	}

	private void showMyProfileCreatedDialog() {
		AlertDialog.Builder builder = new CustomAlertDialogBuilder(AccessibilityPreferencesActivity.this);
		builder.setTitle(R.string.preference_access_title_created_profile);
		builder.setMessage(R.string.preference_access_summary_created_profile);
		builder.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.show();
	}

	public void showFontfaceDialog(View view) {
		final String[] fontfaces = {
				getResources().getString(R.string.preference_access_title_fontface_standard),
				getResources().getString(R.string.preference_access_title_fontface_serif),
				getResources().getString(R.string.preference_access_title_fontface_dyslexic) };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.preference_access_choose_fontface));
		builder.setSingleChoiceItems(fontfaces, selectedFontface, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
					case 0:
						selectedFontface = FONTFACE_STANDARD;
						break;
					case 1:
						selectedFontface = FONTFACE_SERIF;
						break;
					case 2:
						selectedFontface = FONTFACE_DYSLEXIC;
						break;
				}
				if (!selectedProfileName.equals(BaseSettingsActivity.ACCESS_PROFILE_MYPROFILE)) {
					showMyProfileCreatedDialog();
					selectedProfileName = BaseSettingsActivity.ACCESS_PROFILE_MYPROFILE;
					updateAccessibilityActiveProfile();
				}
				if (!inSelectedProfile) {
					changesMade = true;
				}
				saveAccessibilityMyProfilePreference();
				dialog.dismiss();
			}
		});
		builder.show();
	}

	public void showRestartDialog() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case DialogInterface.BUTTON_NEUTRAL:
						saveAccessibilityProfilePreference();
						BaseSettingsActivity.setActiveAccessibilityProfile(getApplicationContext(), selectedProfileName);
						updateAccessibilityActiveProfile();

						BaseSettingsActivity.applyAccessibilitySettings(getApplicationContext());
						Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra(BaseMainMenuActivity.RESTART_INTENT, true);
						startActivity(intent);
						break;
				}
			}
		};
		AlertDialog.Builder builder = new CustomAlertDialogBuilder(AccessibilityPreferencesActivity.this);
		builder.setTitle(R.string.preference_access_title_restart);
		builder.setMessage(R.string.preference_access_summary_restart);
		builder.setNeutralButton(R.string.ok, dialogClickListener);
		builder.show();

		String preferences = getActivePreferences();
		Utils.getTrackingUtilProxy().trackApplyAccessibilityPreferences(selectedProfileName, preferences);
	}

	private String getActivePreferences() {
		String activePreferences = "";
		String largeText = getResources().getString(R.string.preference_access_title_large_text);
		String highContrast = getResources().getString(R.string.preference_access_title_high_contrast);
		String additionalIcons = getResources().getString(R.string.preference_access_title_additional_icons);
		String largeIcons = getResources().getString(R.string.preference_access_title_large_icons);
		String iconContrast = getResources().getString(R.string.preference_access_title_high_contrast_icons);
		String largeElementSpacing = getResources().getString(R.string.preference_access_title_largeelementspacing);
		String starterBricks = getResources().getString(R.string.preference_access_title_starter_bricks);
		String dragDropDelay = getResources().getString(R.string.preference_access_title_dragndrop_delay);

		for (int i = 0; i < adapter.getCount(); i++) {
			AccessibilityCheckbox checkbox = adapter.getItem(i);
			if (!checkbox.value) {
				continue;
			}

			if (checkbox.title.equals(largeText)) {
				activePreferences += largeText + ",";
			} else if (checkbox.title.equals(highContrast)) {
				activePreferences += highContrast + ",";
			} else if (checkbox.title.equals(additionalIcons)) {
				activePreferences += additionalIcons + ",";
			} else if (checkbox.title.equals(largeIcons)) {
				activePreferences += largeIcons + ",";
			} else if (checkbox.title.equals(iconContrast)) {
				activePreferences += iconContrast + ",";
			} else if (checkbox.title.equals(largeElementSpacing)) {
				activePreferences += largeElementSpacing + ",";
			} else if (checkbox.title.equals(starterBricks)) {
				activePreferences += starterBricks + ",";
			} else if (checkbox.title.equals(dragDropDelay)) {
				activePreferences += dragDropDelay + ",";
			}
		}

		if (activePreferences.endsWith(",")) {
			activePreferences = activePreferences.substring(0, activePreferences.length() - 1);
		}

		return activePreferences;
	}

	public void applyProfile(View view) {
		showRestartDialog();
	}

	private void updateAccessibilityActiveProfile() {
		Context context = getApplicationContext();
		TextView activeProfileTitle = (TextView) findViewById(R.id.access_active_profile_title);
		TextView activeProfileSummary = (TextView) findViewById(R.id.access_active_profile_summary);
		ImageView activeProfileImage = (ImageView) findViewById(R.id.access_active_profile_image);

		String profile = "";
		if (inSelectedProfile) {
			profile = selectedProfileName;
		} else {
			profile = BaseSettingsActivity.getActiveAccessibilityProfile(context);
		}

		String title = "";
		String summary = "";
		int image = -1;

		if (profile.equals(BaseSettingsActivity.ACCESS_PROFILE_STANDARD)) {
			title = getResources().getString(R.string.preference_access_title_profile_standard);
			summary = getResources().getString(R.string.preference_access_summary_profile_standard);
			image = R.drawable.nolb_standard_myprofile;
		} else if (profile.equals(BaseSettingsActivity.ACCESS_PROFILE_MYPROFILE)) {
			title = getResources().getString(R.string.preference_access_title_profile_myprofile);
			summary = getResources().getString(R.string.preference_access_summary_profile_myprofile);
			image = R.drawable.nolb_standard_myprofile;
		} else if (profile.equals(BaseSettingsActivity.ACCESS_PROFILE_1)) {
			title = getResources().getString(R.string.preference_access_title_profile_1);
			summary = getResources().getString(R.string.preference_access_summary_profile_1);
			image = R.drawable.nolb_argus;
		} else if (profile.equals(BaseSettingsActivity.ACCESS_PROFILE_2)) {
			title = getResources().getString(R.string.preference_access_title_profile_2);
			summary = getResources().getString(R.string.preference_access_summary_profile_2);
			image = R.drawable.nolb_odin;
		} else if (profile.equals(BaseSettingsActivity.ACCESS_PROFILE_3)) {
			title = getResources().getString(R.string.preference_access_title_profile_3);
			summary = getResources().getString(R.string.preference_access_summary_profile_3);
			image = R.drawable.nolb_fenrir;
		} else if (profile.equals(BaseSettingsActivity.ACCESS_PROFILE_4)) {
			title = getResources().getString(R.string.preference_access_title_profile_4);
			summary = getResources().getString(R.string.preference_access_summary_profile_4);
			image = R.drawable.nolb_tiro;
		}

		activeProfileTitle.setText(title);
		activeProfileSummary.setText(summary);
		activeProfileImage.setImageResource(image);
	}

	private void saveAccessibilityProfilePreference() {
		Context context = getApplicationContext();
		for (int i = 0; i < adapter.getCount(); i++) {
			AccessibilityCheckbox checkbox = adapter.getItem(i);
			if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_large_text))) {
				BaseSettingsActivity.setAccessibilityLargeTextEnabled(context, checkbox.value);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_high_contrast))) {
				BaseSettingsActivity.setAccessibilityHighContrastEnabled(context, checkbox.value);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_additional_icons))) {
				BaseSettingsActivity.setAccessibilityAdditionalIconsEnabled(context, checkbox.value);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_large_icons))) {
				BaseSettingsActivity.setAccessibilityLargeIconsEnabled(context, checkbox.value);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_high_contrast_icons))) {
				BaseSettingsActivity.setAccessibilityHighContrastIconsEnabled(context, checkbox.value);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_largeelementspacing))) {
				BaseSettingsActivity.setAccessibilityLargeElementSpacingEnabled(context, checkbox.value);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_starter_bricks))) {
				BaseSettingsActivity.setAccessibilityStarterBricksEnabled(context, checkbox.value);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_dragndrop_delay))) {
				BaseSettingsActivity.setAccessibilityDragndropDelayEnabled(context, checkbox.value);
			}
		}
		if (selectedFontface == FONTFACE_STANDARD) {
			BaseSettingsActivity.setAccessibilityFontFace(context, BaseSettingsActivity.ACCESS_FONTFACE_VALUE_STANDARD);
		} else if (selectedFontface == FONTFACE_SERIF) {
			BaseSettingsActivity.setAccessibilityFontFace(context, BaseSettingsActivity.ACCESS_FONTFACE_VALUE_SERIF);
		} else if (selectedFontface == FONTFACE_DYSLEXIC) {
			BaseSettingsActivity.setAccessibilityFontFace(context, BaseSettingsActivity.ACCESS_FONTFACE_VALUE_DYSLEXIC);
		}
	}

	private void saveAccessibilityMyProfilePreference() {
		Context context = getApplicationContext();
		for (int i = 0; i < adapter.getCount(); i++) {
			AccessibilityCheckbox checkbox = adapter.getItem(i);
			if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_large_text))) {
				BaseSettingsActivity.setAccessibilityMyProfileLargeTextEnabled(context, checkbox.value);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_high_contrast))) {
				BaseSettingsActivity.setAccessibilityMyProfileHighContrastEnabled(context, checkbox.value);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_additional_icons))) {
				BaseSettingsActivity.setAccessibilityMyProfileAdditionalIconsEnabled(context, checkbox.value);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_large_icons))) {
				BaseSettingsActivity.setAccessibilityMyProfileLargeIconsEnabled(context, checkbox.value);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_high_contrast_icons))) {
				BaseSettingsActivity.setAccessibilityMyProfileHighContrastIconsEnabled(context, checkbox.value);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_largeelementspacing))) {
				BaseSettingsActivity.setAccessibilityMyProfileLargeElementSpacingEnabled(context, checkbox.value);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_starter_bricks))) {
				BaseSettingsActivity.setAccessibilityMyProfileStarterBricksEnabled(context, checkbox.value);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_dragndrop_delay))) {
				BaseSettingsActivity.setAccessibilityMyProfileDragndropDelayEnabled(context, checkbox.value);
			}
		}
		if (selectedFontface == FONTFACE_STANDARD) {
			BaseSettingsActivity.setAccessibilityMyProfileFontFace(context, BaseSettingsActivity.ACCESS_FONTFACE_VALUE_STANDARD);
		} else if (selectedFontface == FONTFACE_SERIF) {
			BaseSettingsActivity.setAccessibilityMyProfileFontFace(context, BaseSettingsActivity.ACCESS_FONTFACE_VALUE_SERIF);
		} else if (selectedFontface == FONTFACE_DYSLEXIC) {
			BaseSettingsActivity.setAccessibilityMyProfileFontFace(context, BaseSettingsActivity.ACCESS_FONTFACE_VALUE_DYSLEXIC);
		}
	}

	private void loadAccessibilityProfilePreference() {
		Context context = getApplicationContext();
		for (int i = 0; i < adapter.getCount(); i++) {
			AccessibilityCheckbox checkbox = adapter.getItem(i);
			if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_large_text))) {
				checkbox.value = BaseSettingsActivity.getAccessibilityLargeTextEnabled(context);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_high_contrast))) {
				checkbox.value = BaseSettingsActivity.getAccessibilityHighContrastEnabled(context);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_additional_icons))) {
				checkbox.value = BaseSettingsActivity.getAccessibilityAdditionalIconsEnabled(context);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_large_icons))) {
				checkbox.value = BaseSettingsActivity.getAccessibilityLargeIconsEnabled(context);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_high_contrast_icons))) {
				checkbox.value = BaseSettingsActivity.getAccessibilityHighContrastIconsEnabled(context);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_largeelementspacing))) {
				checkbox.value = BaseSettingsActivity.getAccessibilityLargeElementSpacingEnabled(context);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_starter_bricks))) {
				checkbox.value = BaseSettingsActivity.getAccessibilityStarterBricksEnabled(context);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_dragndrop_delay))) {
				checkbox.value = BaseSettingsActivity.getAccessibilityDragndropDelayEnabled(context);
			}
		}
		if (BaseSettingsActivity.getAccessibilityFontFace(context).equals(BaseSettingsActivity.ACCESS_FONTFACE_VALUE_STANDARD)) {
			selectedFontface = FONTFACE_STANDARD;
		} else if (BaseSettingsActivity.getAccessibilityFontFace(context).equals(BaseSettingsActivity.ACCESS_FONTFACE_VALUE_SERIF)) {
			selectedFontface = FONTFACE_SERIF;
		} else if (BaseSettingsActivity.getAccessibilityFontFace(context).equals(BaseSettingsActivity.ACCESS_FONTFACE_VALUE_DYSLEXIC)) {
			selectedFontface = FONTFACE_DYSLEXIC;
		}
	}

	private void loadAccessibilityMyProfilePreference() {
		Context context = getApplicationContext();
		for (int i = 0; i < adapter.getCount(); i++) {
			AccessibilityCheckbox checkbox = adapter.getItem(i);
			if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_large_text))) {
				checkbox.value = BaseSettingsActivity.getAccessibilityMyProfileLargeTextEnabled(context);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_high_contrast))) {
				checkbox.value = BaseSettingsActivity.getAccessibilityMyProfileHighContrastEnabled(context);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_additional_icons))) {
				checkbox.value = BaseSettingsActivity.getAccessibilityMyProfileAdditionalIconsEnabled(context);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_large_icons))) {
				checkbox.value = BaseSettingsActivity.getAccessibilityMyProfileLargeIconsEnabled(context);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_high_contrast_icons))) {
				checkbox.value = BaseSettingsActivity.getAccessibilityMyProfileHighContrastIconsEnabled(context);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_largeelementspacing))) {
				checkbox.value = BaseSettingsActivity.getAccessibilityMyProfileLargeElementSpacingEnabled(context);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_starter_bricks))) {
				checkbox.value = BaseSettingsActivity.getAccessibilityMyProfileStarterBricksEnabled(context);
			} else if (checkbox.title.equals(getResources().getString(R.string.preference_access_title_dragndrop_delay))) {
				checkbox.value = BaseSettingsActivity.getAccessibilityMyProfileDragndropDelayEnabled(context);
			}
		}
		if (BaseSettingsActivity.getAccessibilityMyProfileFontFace(context).equals(BaseSettingsActivity.ACCESS_FONTFACE_VALUE_STANDARD)) {
			selectedFontface = FONTFACE_STANDARD;
		} else if (BaseSettingsActivity.getAccessibilityMyProfileFontFace(context).equals(BaseSettingsActivity.ACCESS_FONTFACE_VALUE_SERIF)) {
			selectedFontface = FONTFACE_SERIF;
		} else if (BaseSettingsActivity.getAccessibilityMyProfileFontFace(context).equals(BaseSettingsActivity.ACCESS_FONTFACE_VALUE_DYSLEXIC)) {
			selectedFontface = FONTFACE_DYSLEXIC;
		}
	}

	private void loadProfile(int profileId) {
		if (profileId == R.id.access_profilestandard) {
			selectedProfileName = BaseSettingsActivity.ACCESS_PROFILE_STANDARD;
			parseAccessibilityPredefinedProfile(BaseSettingsActivity.ACCESS_PROFILE_STANDARD);
		} else if (profileId == R.id.access_profilemyprofile) {
			selectedProfileName = BaseSettingsActivity.ACCESS_PROFILE_MYPROFILE;
			loadAccessibilityMyProfilePreference();
		} else if (profileId == R.id.access_profile1) {
			selectedProfileName = BaseSettingsActivity.ACCESS_PROFILE_1;
			parseAccessibilityPredefinedProfile(BaseSettingsActivity.ACCESS_PROFILE_1);
		} else if (profileId == R.id.access_profile2) {
			selectedProfileName = BaseSettingsActivity.ACCESS_PROFILE_2;
			parseAccessibilityPredefinedProfile(BaseSettingsActivity.ACCESS_PROFILE_2);
		} else if (profileId == R.id.access_profile3) {
			selectedProfileName = BaseSettingsActivity.ACCESS_PROFILE_3;
			parseAccessibilityPredefinedProfile(BaseSettingsActivity.ACCESS_PROFILE_3);
		} else if (profileId == R.id.access_profile4) {
			selectedProfileName = BaseSettingsActivity.ACCESS_PROFILE_4;
			parseAccessibilityPredefinedProfile(BaseSettingsActivity.ACCESS_PROFILE_4);
		}
	}

	private void parseAccessibilityPredefinedProfile(String profile) {
		try {
			XmlPullParser parser = getResources().getXml(R.xml.accessibility_profiles);

			String parsedProfile = "";
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				switch (event) {
					case XmlPullParser.START_TAG:
						String tagName = parser.getName();
						if (tagName.equals(XML_PROFILE_TAG)) {
							parsedProfile = parser.getAttributeValue(null, XML_NAME_FIELD);
						}
						if (profile.equals(parsedProfile)) {
							parseAccessibilityProfileAttribute(parser, tagName);
						}
						break;
					case XmlPullParser.TEXT:
						break;
					case XmlPullParser.END_TAG:
						break;
					default:
						break;
				}
				event = parser.next();
			}
		} catch (XmlPullParserException ex) {
			Log.e(TAG, Log.getStackTraceString(ex));
		} catch (IOException ex) {
			Log.e(TAG, Log.getStackTraceString(ex));
		}
	}

	private void parseAccessibilityProfileAttribute(XmlPullParser parser, String tagName) {
		if (!(tagName.equals(XML_PARAMETER_TAG))) {
			return;
		}

		String attributeName = parser.getAttributeValue(null, XML_NAME_FIELD);
		boolean checkBoxValue = Boolean.parseBoolean(parser.getAttributeValue(null, XML_VALUE_FIELD));
		if (attributeName.equals(BaseSettingsActivity.ACCESS_LARGE_TEXT)) {
			AccessibilityCheckbox checkBox = getAccessibilityCheckBox(getResources().getString(
					R.string.preference_access_title_large_text));
			if (checkBox != null) {
				checkBox.value = checkBoxValue;
			}
		} else if (attributeName.equals(BaseSettingsActivity.ACCESS_HIGH_CONTRAST)) {
			AccessibilityCheckbox checkBox = getAccessibilityCheckBox(getResources().getString(
					R.string.preference_access_title_high_contrast));
			if (checkBox != null) {
				checkBox.value = checkBoxValue;
			}
		} else if (attributeName.equals(BaseSettingsActivity.ACCESS_ADDITIONAL_ICONS)) {
			AccessibilityCheckbox checkBox = getAccessibilityCheckBox(getResources().getString(
					R.string.preference_access_title_additional_icons));
			if (checkBox != null) {
				checkBox.value = checkBoxValue;
			}
		} else if (attributeName.equals(BaseSettingsActivity.ACCESS_LARGE_ICONS)) {
			AccessibilityCheckbox checkBox = getAccessibilityCheckBox(getResources().getString(
					R.string.preference_access_title_large_icons));
			if (checkBox != null) {
				checkBox.value = checkBoxValue;
			}
		} else if (attributeName.equals(BaseSettingsActivity.ACCESS_HIGH_CONTRAST_ICONS)) {
			AccessibilityCheckbox checkBox = getAccessibilityCheckBox(getResources().getString(
					R.string.preference_access_title_high_contrast_icons));
			if (checkBox != null) {
				checkBox.value = checkBoxValue;
			}
		} else if (attributeName.equals(BaseSettingsActivity.ACCESS_LARGE_ELEMENT_SPACING)) {
			AccessibilityCheckbox checkBox = getAccessibilityCheckBox(getResources().getString(
					R.string.preference_access_title_largeelementspacing));
			if (checkBox != null) {
				checkBox.value = checkBoxValue;
			}
		} else if (attributeName.equals(BaseSettingsActivity.ACCESS_STARTER_BRICKS)) {
			AccessibilityCheckbox checkBox = getAccessibilityCheckBox(getResources().getString(
					R.string.preference_access_title_starter_bricks));
			if (checkBox != null) {
				checkBox.value = checkBoxValue;
			}
		} else if (attributeName.equals(BaseSettingsActivity.ACCESS_DRAGNDROP_DELAY)) {
			AccessibilityCheckbox checkBox = getAccessibilityCheckBox(getResources().getString(
					R.string.preference_access_title_dragndrop_delay));
			if (checkBox != null) {
				checkBox.value = checkBoxValue;
			}
		} else if (attributeName.equals(BaseSettingsActivity.ACCESS_FONTFACE)) {
			String fontface = parser.getAttributeValue(null, XML_VALUE_FIELD);
			if (fontface.equals(BaseSettingsActivity.ACCESS_FONTFACE_VALUE_STANDARD)) {
				selectedFontface = FONTFACE_STANDARD;
			} else if (fontface.equals(BaseSettingsActivity.ACCESS_FONTFACE_VALUE_SERIF)) {
				selectedFontface = FONTFACE_SERIF;
			} else if (fontface.equals(BaseSettingsActivity.ACCESS_FONTFACE_VALUE_DYSLEXIC)) {
				selectedFontface = FONTFACE_DYSLEXIC;
			}
		}
	}

	private AccessibilityCheckbox getAccessibilityCheckBox(String title) {
		for (int i = 0; i < adapter.getCount(); i++) {
			if (title.equals(adapter.getItem(i).title)) {
				return adapter.getItem(i);
			}
		}
		return null;
	}
}
