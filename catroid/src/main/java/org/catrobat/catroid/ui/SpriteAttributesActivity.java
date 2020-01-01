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

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.IntDef;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.recyclerview.RVButton;
import org.catrobat.catroid.ui.recyclerview.adapter.ButtonAdapter;
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.RenameItemTextWatcher;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class SpriteAttributesActivity extends BaseActivity implements ButtonAdapter.OnItemClickListener {

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({SCRIPTS, LOOKS, SOUNDS, NFC_TAGS})
	@interface ButtonId {}
	private static final int SCRIPTS = 0;
	private static final int LOOKS = 1;
	private static final int SOUNDS = 2;
	private static final int NFC_TAGS = 3;

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

		RecyclerView recyclerView = findViewById(R.id.recycler_view);
		List<RVButton> items = getItems();
		ButtonAdapter adapter = new ButtonAdapter(items);
		adapter.setOnItemClickListener(this);
		recyclerView.setAdapter(adapter);
		recyclerView.addItemDecoration(
				new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

		BottomBar.hideAddButton(this);
		updateActionBarTitle();
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

	private List<RVButton> getItems() {
		List<RVButton> items = new ArrayList<>();
		items.add(new RVButton(SCRIPTS, ContextCompat.getDrawable(this, R.drawable.ic_program_menu_scripts),
				getString(R.string.scripts)));

		Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		if (currentSprite.equals(currentScene.getBackgroundSprite())) {
			items.add(new RVButton(LOOKS, ContextCompat.getDrawable(this, R.drawable.ic_program_menu_looks),
					getString(R.string.backgrounds)));
		} else {
			items.add(new RVButton(LOOKS, ContextCompat.getDrawable(this, R.drawable.ic_program_menu_looks),
					getString(R.string.looks)));
		}
		items.add(new RVButton(SOUNDS, ContextCompat.getDrawable(this, R.drawable.ic_program_menu_sounds),
				getString(R.string.sounds)));

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (sharedPreferences.getBoolean("setting_nfc_bricks", false) && BuildConfig.FEATURE_NFC_ENABLED) {
			items.add(new RVButton(NFC_TAGS, ContextCompat.getDrawable(this, R.drawable.ic_program_menu_nfc),
					getString(R.string.nfctags)));
		}
		return items;
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
		Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		if (currentSprite.equals(currentScene.getBackgroundSprite())) {
			return super.onCreateOptionsMenu(menu);
		}
		getMenuInflater().inflate(R.menu.menu_program_menu, menu);
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

	@Override
	public void onItemClick(@ButtonId int id) {
		switch (id) {
			case SCRIPTS:
				startScriptActivity(SpriteActivity.FRAGMENT_SCRIPTS);
				break;
			case LOOKS:
				startScriptActivity(SpriteActivity.FRAGMENT_LOOKS);
				break;
			case SOUNDS:
				startScriptActivity(SpriteActivity.FRAGMENT_SOUNDS);
				break;
			case NFC_TAGS:
				startScriptActivity(SpriteActivity.FRAGMENT_NFC_TAGS);
				break;
		}
	}

	private void startScriptActivity(int fragmentPosition) {
		Intent intent = new Intent(this, SpriteActivity.class);
		intent.putExtra(SpriteActivity.EXTRA_FRAGMENT_POSITION, fragmentPosition);
		startActivity(intent);
	}

	public void handlePlayButton(View view) {
		StageActivity.handlePlayButton(ProjectManager.getInstance(), this);
	}
}
