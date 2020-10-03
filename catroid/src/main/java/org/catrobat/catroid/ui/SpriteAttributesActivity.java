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

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.cast.CastManager;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.TestResult;
import org.catrobat.catroid.ui.adapter.ViewPagerAdapter;
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.RenameItemTextWatcher;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.utils.ToastUtil;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import static org.catrobat.catroid.stage.TestResult.TEST_RESULT_MESSAGE;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.isCastSharedPreferenceEnabled;

public class SpriteAttributesActivity extends BaseActivity {

	private static final int[] TAB_ICONS = {
			R.drawable.ic_program_menu_scripts,
			R.drawable.ic_program_menu_looks,
			R.drawable.ic_program_menu_sounds
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (isFinishing()) {
			return;
		}

		SettingsFragment.setToChosenLanguage(this);

		setContentView(R.layout.activity_sprite_attributes);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setUpViewPagerWithTabLayout();

		updateActionBarTitle();

		handlePlayButton();
	}

	private void setUpViewPagerWithTabLayout() {
		ViewPager viewPager = findViewById(R.id.view_pager);
		TabLayout tabLayout = findViewById(R.id.tab_layout);
		ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(viewPagerAdapter);
		tabLayout.setupWithViewPager(viewPager);

		for (int i = 0; i < tabLayout.getTabCount(); i++) {
			tabLayout.getTabAt(i).setIcon(TAB_ICONS[i]);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == TestResult.STAGE_ACTIVITY_TEST_SUCCESS
				|| resultCode == TestResult.STAGE_ACTIVITY_TEST_FAIL) {
			String message = data.getStringExtra(TEST_RESULT_MESSAGE);
			ToastUtil.showError(this, message);
			ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			ClipData testResult = ClipData.newPlainText("TestResult",
					ProjectManager.getInstance().getCurrentProject().getName() + "\n" + message);
			clipboard.setPrimaryClip(testResult);
		}

		if (resultCode != RESULT_OK) {
			if (isCastSharedPreferenceEnabled(this)
					&& ProjectManager.getInstance().getCurrentProject().isCastProject()
					&& !CastManager.getInstance().isConnected()) {

				CastManager.getInstance().openDeviceSelectorOrDisconnectDialog(this);
			}
			return;
		}
	}

	private void updateActionBarTitle() {
		Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		if (ProjectManager.getInstance().getCurrentProject().getSceneList().size() == 1) {
			getSupportActionBar().setTitle(currentSprite.getName());
		} else {
			getSupportActionBar().setTitle(currentScene.getName() + ": " + currentSprite.getName());
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_rename_sprite:
				showRenameDialog();
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_script_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void showRenameDialog() {
		final Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();
		final Sprite item = ProjectManager.getInstance().getCurrentSprite();

		TextInputDialog.Builder builder = new TextInputDialog.Builder(this);

		builder.setHint(getString(R.string.sprite_name_label))
				.setText(item.getName())
				.setTextWatcher(new RenameItemTextWatcher<>(item, currentScene.getSpriteList()))
				.setPositiveButton(getString(R.string.rename), new TextInputDialog.OnClickListener() {
					@Override
					public void onPositiveButtonClick(DialogInterface dialog, String textInput) {
						renameItem(item, textInput);
					}
				});

		builder.setTitle(R.string.rename_sprite_dialog)
				.setNegativeButton(R.string.cancel, null)
				.show();
	}

	private void renameItem(Sprite item, String name) {
		if (!item.getName().equals(name)) {
			item.setName(name);
		}
		updateActionBarTitle();
	}

	public void handlePlayButton() {
		findViewById(R.id.button_play).setOnClickListener(v ->
				StageActivity.handlePlayButton(ProjectManager.getInstance(), this));
	}
}
