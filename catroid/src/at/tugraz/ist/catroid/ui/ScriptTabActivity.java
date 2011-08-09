/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.ui;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.dialogs.RenameCostumeDialog;
import at.tugraz.ist.catroid.ui.dialogs.RenameSoundDialog;
import at.tugraz.ist.catroid.utils.ActivityHelper;

public class ScriptTabActivity extends TabActivity {
	protected ActivityHelper activityHelper;

	private TabHost tabHost;
	public SoundInfo selectedSoundInfo;
	private RenameSoundDialog renameSoundDialog;
	public CostumeData selectedCostumeData;
	private RenameCostumeDialog renameCostumeDialog;

	private void setupTabHost() {
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scripttab);

		setupTabHost();
		tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

		Intent intent; // Reusable Intent for each tab

		intent = new Intent().setClass(this, ScriptActivity.class);
		setupTab(new TextView(this), this.getString(R.string.scripts), intent);
		intent = new Intent().setClass(this, CostumeActivity.class);
		setupTab(new TextView(this), this.getString(R.string.costumes), intent);
		intent = new Intent().setClass(this, SoundActivity.class);
		setupTab(new TextView(this), this.getString(R.string.sounds), intent);

		setUpActionBar();
		if (getLastNonConfigurationInstance() != null) {
			selectedCostumeData = (CostumeData) ((Pair<?, ?>) getLastNonConfigurationInstance()).first;
			selectedSoundInfo = (SoundInfo) ((Pair<?, ?>) getLastNonConfigurationInstance()).second;
		}

	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		final Pair<CostumeData, SoundInfo> savedCostumeDataAndSoundInfo = new Pair<CostumeData, SoundInfo>(
				selectedCostumeData, selectedSoundInfo);
		return savedCostumeDataAndSoundInfo;
	}

	private void setUpActionBar() {
		activityHelper = new ActivityHelper(this);

		String title = this.getResources().getString(R.string.sprite_name) + " "
				+ ProjectManager.getInstance().getCurrentSprite().getName();
		activityHelper.setupActionBar(false, title);

		activityHelper.addActionButton(R.id.btn_action_add_sprite, R.drawable.ic_plus_black, null, false);

		activityHelper.addActionButton(R.id.btn_action_play, R.drawable.ic_play_black, new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ScriptTabActivity.this, StageActivity.class);
				startActivity(intent);
			}
		}, false);
	}

	private void setupTab(final View view, final String tag, Intent intent) {
		View tabview = createTabView(tabHost.getContext(), tag);

		TabSpec setContent = tabHost.newTabSpec(tag).setIndicator(tabview).setContent(intent);
		tabHost.addTab(setContent);

	}

	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.activity_tabscriptactivity_tabs, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		final Dialog dialog;
		switch (id) {
			case Consts.DIALOG_RENAME_SOUND:
				if (selectedSoundInfo == null) {
					dialog = null;
				} else {
					renameSoundDialog = new RenameSoundDialog(this);
					dialog = renameSoundDialog.createDialog(selectedSoundInfo);
				}
				break;
			case Consts.DIALOG_RENAME_COSTUME:
				if (selectedCostumeData == null) {
					dialog = null;
				} else {
					renameCostumeDialog = new RenameCostumeDialog(this);
					dialog = renameCostumeDialog.createDialog(selectedCostumeData);
				}
				break;
			default:
				dialog = null;
				break;
		}
		return dialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
			case Consts.DIALOG_RENAME_SOUND:
				EditText soundTitleInput = (EditText) dialog.findViewById(R.id.dialog_rename_sound_editText);
				soundTitleInput.setText(selectedSoundInfo.getTitle());
				break;
			case Consts.DIALOG_RENAME_COSTUME:
				EditText costumeTitleInput = (EditText) dialog.findViewById(R.id.dialog_rename_costume_editText);
				costumeTitleInput.setText(selectedCostumeData.getCostumeName());
				break;
		}
	}

	public void handlePositiveButtonRenameSound(View v) {
		renameSoundDialog.handleOkButton();
	}

	public void handleNegativeButtonRenameSound(View v) {
		dismissDialog(Consts.DIALOG_RENAME_SOUND);
	}

	public void handlePositiveButtonRenameCostume(View v) {
		renameCostumeDialog.handleOkButton();
	}

	public void handleNegativeButtonRenameCostume(View v) {
		dismissDialog(Consts.DIALOG_RENAME_COSTUME);
	}

}
