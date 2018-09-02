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
package org.catrobat.catroid.ui;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookSdk;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.cast.CastManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.ui.dialogs.TermsOfUseDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.AboutDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.PrivacyPolicyDialogFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.MainMenuFragment;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.utils.ScreenValueHandler;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import static org.catrobat.catroid.common.Constants.PREF_PROJECTNAME_KEY;
import static org.catrobat.catroid.common.SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_PREFERENCE_KEY;

public class MainMenuActivity extends BaseCastActivity {

	public static final String TAG = MainMenuActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SettingsFragment.setToChosenLanguage(this);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
		ScreenValueHandler.updateScreenWidthAndHeight(this);

		boolean hasUserAgreedToPrivacyPolicy = PreferenceManager.getDefaultSharedPreferences(this)
				.getBoolean(AGREED_TO_PRIVACY_POLICY_PREFERENCE_KEY, false);

		if (hasUserAgreedToPrivacyPolicy) {
			loadContent();
		} else {
			setContentView(R.layout.privacy_policy_view);
		}
	}

	public void handleAgreedToPrivacyPolicyButton(View view) {
		PreferenceManager.getDefaultSharedPreferences(this)
				.edit()
				.putBoolean(AGREED_TO_PRIVACY_POLICY_PREFERENCE_KEY, true)
				.commit();

		loadContent();
	}

	public void handleDeclinedPrivacyPolicyButton(View view) {
		View dialogView = View.inflate(this, R.layout.declined_privacy_agreement_alert_view, null);

		String linkString = getString(
				R.string.about_link_template, Constants.CATROBAT_ABOUT_URL, getString(R.string.share_website_text));

		((TextView) dialogView.findViewById(R.id.share_website_view)).setText(Html.fromHtml(linkString));

		new AlertDialog.Builder(this)
				.setView(dialogView)
				.setNeutralButton(R.string.ok, null)
				.create()
				.show();
	}

	private void loadContent() {
		FacebookSdk.sdkInitialize(getApplicationContext());

		setContentView(R.layout.activity_main_menu);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setIcon(R.drawable.pc_toolbar_icon);
		getSupportActionBar().setTitle(R.string.app_name);

		getFragmentManager().beginTransaction()
				.replace(R.id.fragment_container, new MainMenuFragment(), MainMenuFragment.TAG)
				.commit();

		if (SettingsFragment.isCastSharedPreferenceEnabled(this)) {
			CastManager.getInstance().initializeCast(this);
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		Project currentProject = ProjectManager.getInstance().getCurrentProject();

		if (currentProject != null && checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})) {

			ProjectManager.getInstance().saveProject(getApplicationContext());
			PreferenceManager.getDefaultSharedPreferences(this)
					.edit()
					.putString(PREF_PROJECTNAME_KEY, currentProject.getName())
					.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main_menu, menu);

		String scratchConverter = getString(R.string.main_menu_scratch_converter);
		SpannableString scratchConverterBeta = new SpannableString(scratchConverter
				+ " "
				+ getString(R.string.beta));
		scratchConverterBeta.setSpan(
				new ForegroundColorSpan(getResources().getColor(R.color.beta_label_color)),
				scratchConverter.length(), scratchConverterBeta.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		menu.findItem(R.id.menu_scratch_converter).setTitle(scratchConverterBeta);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_login).setVisible(!Utils.isUserLoggedIn(this));
		menu.findItem(R.id.menu_logout).setVisible(Utils.isUserLoggedIn(this));
		if (!BuildConfig.FEATURE_SCRATCH_CONVERTER_ENABLED) {
			menu.removeItem(R.id.menu_scratch_converter);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_rate_app:
				if (Utils.isNetworkAvailable(this)) {
					try {
						startActivity(new Intent(Intent.ACTION_VIEW,
								Uri.parse("market://details?id=" + getPackageName())));
					} catch (ActivityNotFoundException e) {
						ToastUtil.showError(this, R.string.main_menu_play_store_not_installed);
					}
				} else {
					ToastUtil.showError(this, R.string.error_internet_connection);
				}
				break;
			case R.id.menu_terms_of_use:
				new TermsOfUseDialogFragment().show(getFragmentManager(), TermsOfUseDialogFragment.TAG);
				break;
			case R.id.menu_privacy_policy:
				new PrivacyPolicyDialogFragment().show(getFragmentManager(), PrivacyPolicyDialogFragment.TAG);
				break;
			case R.id.menu_about:
				new AboutDialogFragment().show(getFragmentManager(), AboutDialogFragment.TAG);
				break;
			case R.id.menu_scratch_converter:
				if (Utils.isNetworkAvailable(this)) {
					startActivity(new Intent(this, ScratchConverterActivity.class));
				} else {
					ToastUtil.showError(this, R.string.error_internet_connection);
				}
				break;
			case R.id.settings:
				startActivity(new Intent(this, SettingsActivity.class));
				break;
			case R.id.menu_login:
				startActivity(new Intent(this, SignInActivity.class));
				break;
			case R.id.menu_logout:
				Utils.logoutUser(this);
				ToastUtil.showSuccess(this, R.string.logout_successful);
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}
}
