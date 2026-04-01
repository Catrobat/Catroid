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

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.utils.ToastUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import static org.catrobat.catroid.common.SharedPreferenceKeys.ACCESSIBILITY_PROFILE_PREFERENCE_KEY;
import static org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.BEGINNER_BRICKS;
import static org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.DRAGNDROP_DELAY;
import static org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.ELEMENT_SPACING;
import static org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.HIGH_CONTRAST;
import static org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.ICONS;
import static org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.LARGE_ICONS;
import static org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.LARGE_TEXT;
import static org.catrobat.catroid.ui.settingsfragments.AccessibilitySettingsFragment.CUSTOM_PROFILE;

public class AccessibilityProfilesFragment extends Fragment implements View.OnClickListener {

	public static final String TAG = AccessibilityProfilesFragment.class.getSimpleName();
	public static final String SETTINGS_FRAGMENT_INTENT_KEY = "rollBackToAccessibilityFragment";

	private View parent;

	private class AccessibilityProfileViewHolder {

		View view;
		RadioButton radioButton;
		ImageView imageView;
		TextView title;
		TextView subtitle;

		AccessibilityProfileViewHolder(View view) {
			this.view = view;
			radioButton = view.findViewById(R.id.radio_button);
			imageView = view.findViewById(R.id.image_view);
			title = view.findViewById(R.id.title_view);
			subtitle = view.findViewById(R.id.subtitle_view);
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		parent = inflater.inflate(R.layout.fragment_accesibility_profiles, container, false);
		return parent;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		AccessibilityProfileViewHolder viewHolder = new AccessibilityProfileViewHolder(parent.findViewById(R.id.custom_profile));
		viewHolder.view.setOnClickListener(this);
		viewHolder.imageView
				.setImageDrawable(ContextCompat.getDrawable(parent.getContext(), R.drawable.nolb_default_myprofile));
		viewHolder.title.setText(R.string.preference_access_title_profile_custom);
		viewHolder.subtitle.setText(R.string.preference_access_summary_profile_custom);

		viewHolder = new AccessibilityProfileViewHolder(parent.findViewById(R.id.default_profile));
		viewHolder.view.setOnClickListener(this);
		viewHolder.imageView
				.setImageDrawable(ContextCompat.getDrawable(parent.getContext(), R.drawable.nolb_default_myprofile));
		viewHolder.title.setText(R.string.preference_access_title_profile_default);
		viewHolder.subtitle.setText(R.string.preference_access_summary_profile_default);

		viewHolder = new AccessibilityProfileViewHolder(parent.findViewById(R.id.argus));
		viewHolder.view.setOnClickListener(this);
		viewHolder.imageView.setImageDrawable(ContextCompat.getDrawable(parent.getContext(), R.drawable.nolb_argus));
		viewHolder.title.setText(R.string.preference_access_title_profile_argus);
		viewHolder.subtitle.setText(R.string.preference_access_summary_profile_argus);

		viewHolder = new AccessibilityProfileViewHolder(parent.findViewById(R.id.fenrir));
		viewHolder.view.setOnClickListener(this);
		viewHolder.imageView.setImageDrawable(ContextCompat.getDrawable(parent.getContext(), R.drawable.nolb_fenrir));
		viewHolder.title.setText(R.string.preference_access_title_profile_fenrir);
		viewHolder.subtitle.setText(R.string.preference_access_summary_profile_fenrir);

		viewHolder = new AccessibilityProfileViewHolder(parent.findViewById(R.id.odin));
		viewHolder.view.setOnClickListener(this);
		viewHolder.imageView.setImageDrawable(ContextCompat.getDrawable(parent.getContext(), R.drawable.nolb_odin));
		viewHolder.title.setText(R.string.preference_access_title_profile_odin);
		viewHolder.subtitle.setText(R.string.preference_access_summary_profile_odin);

		viewHolder = new AccessibilityProfileViewHolder(parent.findViewById(R.id.tiro));
		viewHolder.view.setOnClickListener(this);
		viewHolder.imageView.setImageDrawable(ContextCompat.getDrawable(parent.getContext(), R.drawable.nolb_tiro));
		viewHolder.title.setText(R.string.preference_access_title_profile_tiro);
		viewHolder.subtitle.setText(R.string.preference_access_summary_profile_tiro);

		int selectedProfileViewId = PreferenceManager.getDefaultSharedPreferences(getActivity())
				.getInt(ACCESSIBILITY_PROFILE_PREFERENCE_KEY, R.id.default_profile);

		new AccessibilityProfileViewHolder(parent.findViewById(selectedProfileViewId)).radioButton.setChecked(true);
	}

	@Override
	public void onClick(View v) {
		new AccessibilityProfileViewHolder(v).radioButton.setChecked(true);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		AccessibilityProfile currentProfile = AccessibilityProfile.fromCurrentPreferences(sharedPreferences);
		if (sharedPreferences.getBoolean(CUSTOM_PROFILE, false)) {
			currentProfile.saveAsCustomProfile(sharedPreferences);
			sharedPreferences.edit()
					.putBoolean(CUSTOM_PROFILE, false)
					.apply();
		}

		AccessibilityProfile newProfile;
		switch (v.getId()) {
			case R.id.argus:
				newProfile = new AccessibilityProfile(HIGH_CONTRAST, ICONS);
				break;
			case R.id.fenrir:
				newProfile = new AccessibilityProfile(ELEMENT_SPACING, DRAGNDROP_DELAY);
				break;
			case R.id.odin:
				newProfile = new AccessibilityProfile(LARGE_TEXT, HIGH_CONTRAST, ICONS, LARGE_ICONS, ELEMENT_SPACING);
				break;
			case R.id.tiro:
				newProfile = new AccessibilityProfile(BEGINNER_BRICKS);
				break;
			case R.id.custom_profile:
				newProfile = AccessibilityProfile.fromCustomProfile(sharedPreferences);
				sharedPreferences.edit()
						.putBoolean(CUSTOM_PROFILE, true)
						.apply();
				break;
			case R.id.default_profile:
			default:
				newProfile = new AccessibilityProfile();
		}

		sharedPreferences.edit()
				.putInt(ACCESSIBILITY_PROFILE_PREFERENCE_KEY, v.getId())
				.apply();

		newProfile.setAsCurrent(sharedPreferences);
		startActivity(new Intent(getActivity().getBaseContext(), MainMenuActivity.class));
		Intent settingsIntent = new Intent(getActivity().getBaseContext(), SettingsActivity.class);
		settingsIntent.putExtra(SETTINGS_FRAGMENT_INTENT_KEY, true);
		startActivity(settingsIntent);
		ToastUtil.showSuccess(getActivity(), getString(R.string.accessibility_settings_applied));
		getActivity().finishAffinity();
	}

	@Override
	public void onResume() {
		super.onResume();
		((AppCompatActivity) getActivity()).getSupportActionBar()
				.setTitle(R.string.preference_title_accessibility_profiles);
	}
}
