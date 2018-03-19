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

package org.catrobat.catroid.ui.settingsfragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.recyclerview.SimpleRVItem;
import org.catrobat.catroid.ui.recyclerview.adapter.SimpleRVAdapter;
import org.catrobat.catroid.ui.recyclerview.viewholder.SimpleVH;
import org.catrobat.catroid.utils.ToastUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.BEGINNER_BRICKS;
import static org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.DRAGNDROP_DELAY;
import static org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.ELEMENT_SPACING;
import static org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.HIGH_CONTRAST;
import static org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.ICONS;
import static org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.LARGE_ICONS;
import static org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.LARGE_TEXT;
import static org.catrobat.catroid.ui.settingsfragments.AccessibilitySettingsFragment.CUSTOM_PROFILE;

public class AccessibilityProfilesFragment extends Fragment implements SimpleRVAdapter.OnItemClickListener {
	public static final String TAG = AccessibilityProfilesFragment.class.getSimpleName();

	private RecyclerView recyclerView;

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({DEFAULT, ARGUS, FENRIR, ODIN, TIRO, CUSTOM})
	@interface ProfileId {}
	private static final int CUSTOM = 0;
	private static final int DEFAULT = 1;
	private static final int ARGUS = 2;
	private static final int FENRIR = 3;
	private static final int ODIN = 4;
	private static final int TIRO = 5;

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({WITH_HEADLINE, NO_HEADLINE})
	@interface ProfileViewType {}
	private static final int WITH_HEADLINE = 0;
	private static final int NO_HEADLINE = 1;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.fragment_list_view, container, false);
		recyclerView = parent.findViewById(R.id.recycler_view);
		return parent;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		SimpleRVAdapter adapter;
		List<SimpleRVItem> items = getItems();
		adapter = new SimpleRVAdapter(items) {
			@Override
			public SimpleVH onCreateViewHolder(ViewGroup parent, @ProfileViewType int viewType) {
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				switch (viewType) {
					case NO_HEADLINE:
						View view = inflater.inflate(R.layout.vh_simple, parent, false);
						return new SimpleVH(view);
					case WITH_HEADLINE:
						view = inflater.inflate(R.layout.vh_profile_with_headline, parent, false);
						return new SimpleVH(view);
					default:
						throw new IllegalArgumentException(TAG + ": viewType was not defined correctly.");
				}
			}

			@Override
			public void onBindViewHolder(SimpleVH holder, int position) {
				super.onBindViewHolder(holder, position);
				if (position == 0) {
					((TextView) holder.itemView.findViewById(R.id.headline))
							.setText(R.string.preference_title_accessibility_profile_headline);
				}
				if (position == 2) {
					((TextView) holder.itemView.findViewById(R.id.headline))
							.setText(R.string.preference_title_accessibility_predefined_profile_headline);
				}
			}

			@Override
			public @ProfileViewType int getItemViewType(@ProfileId int position) {
				if (position == 0 || position == 2) {
					return WITH_HEADLINE;
				}
				return NO_HEADLINE;
			}
		};
		adapter.setOnItemClickListener(this);
		recyclerView.setAdapter(adapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		((AppCompatActivity) getActivity()).getSupportActionBar()
				.setTitle(R.string.preference_title_accessibility_profiles);
	}

	@Override
	public void onItemClick(@ProfileId int profileId) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		AccessibilityProfile currentProfile = AccessibilityProfile.fromCurrentPreferences(sharedPreferences);
		if (sharedPreferences.getBoolean(CUSTOM_PROFILE, false)) {
			currentProfile.saveAsCustomProfile(sharedPreferences);
			sharedPreferences.edit().putBoolean(CUSTOM_PROFILE, false).commit();
		}
		AccessibilityProfile newProfile;
		switch (profileId) {
			case ARGUS:
				newProfile = new AccessibilityProfile(HIGH_CONTRAST, ICONS);
				break;
			case FENRIR:
				newProfile = new AccessibilityProfile(ELEMENT_SPACING, DRAGNDROP_DELAY);
				break;
			case ODIN:
				newProfile = new AccessibilityProfile(LARGE_TEXT, HIGH_CONTRAST, ICONS, LARGE_ICONS, ELEMENT_SPACING);
				break;
			case TIRO:
				newProfile = new AccessibilityProfile(BEGINNER_BRICKS);
				break;
			case CUSTOM:
				newProfile = AccessibilityProfile.fromCustomProfile(sharedPreferences);
				sharedPreferences.edit().putBoolean(CUSTOM_PROFILE, true).commit();
				break;
			case DEFAULT:
			default:
				newProfile = new AccessibilityProfile();
				break;
		}
		newProfile.setAsCurrent(sharedPreferences);
		startActivity(new Intent(getActivity().getBaseContext(), MainMenuActivity.class));
		startActivity(new Intent(getActivity().getBaseContext(), SettingsActivity.class));
		ToastUtil.showSuccess(getActivity(), getString(R.string.accessibility_settings_applied));
		getActivity().finishAffinity();
	}

	private List<SimpleRVItem> getItems() {
		List<SimpleRVItem> items = new ArrayList<>();
		items.add(new SimpleRVItem(CUSTOM,
				ContextCompat.getDrawable(getActivity(), R.drawable.nolb_default_myprofile),
				getString(R.string.preference_access_title_profile_custom),
				getString(R.string.preference_access_summary_profile_custom)));
		items.add(new SimpleRVItem(DEFAULT,
				ContextCompat.getDrawable(getActivity(), R.drawable.nolb_default_myprofile),
				getString(R.string.preference_access_title_profile_default),
				getString(R.string.preference_access_summary_profile_default)));
		items.add(new SimpleRVItem(ARGUS,
				ContextCompat.getDrawable(getActivity(), R.drawable.nolb_argus),
				getString(R.string.preference_access_title_profile_argus),
				getString(R.string.preference_access_summary_profile_argus)));
		items.add(new SimpleRVItem(FENRIR,
				ContextCompat.getDrawable(getActivity(), R.drawable.nolb_fenrir),
				getString(R.string.preference_access_title_profile_fenrir),
				getString(R.string.preference_access_summary_profile_fenrir)));
		items.add(new SimpleRVItem(ODIN,
				ContextCompat.getDrawable(getActivity(), R.drawable.nolb_odin),
				getString(R.string.preference_access_title_profile_odin),
				getString(R.string.preference_access_summary_profile_odin)));
		items.add(new SimpleRVItem(TIRO,
				ContextCompat.getDrawable(getActivity(), R.drawable.nolb_tiro),
				getString(R.string.preference_access_title_profile_tiro),
				getString(R.string.preference_access_summary_profile_tiro)));
		return items;
	}
}
