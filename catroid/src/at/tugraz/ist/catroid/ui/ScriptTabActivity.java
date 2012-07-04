/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.stage.PreStageActivity;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.adapter.CostumeAdapter;
import at.tugraz.ist.catroid.ui.adapter.SoundAdapter;
import at.tugraz.ist.catroid.ui.adapter.TabsPagerAdapter;
import at.tugraz.ist.catroid.ui.dialogs.AddBrickDialog;
import at.tugraz.ist.catroid.ui.dialogs.BrickCategoryDialog;
import at.tugraz.ist.catroid.ui.dialogs.DeleteCostumeDialog;
import at.tugraz.ist.catroid.ui.dialogs.DeleteSoundDialog;
import at.tugraz.ist.catroid.ui.dialogs.RenameCostumeDialog;
import at.tugraz.ist.catroid.ui.dialogs.RenameSoundDialog;
import at.tugraz.ist.catroid.ui.fragment.CostumeFragment;
import at.tugraz.ist.catroid.ui.fragment.ScriptFragment;
import at.tugraz.ist.catroid.ui.fragment.SoundFragment;
import at.tugraz.ist.catroid.utils.Utils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ScriptTabActivity extends SherlockFragmentActivity implements OnDismissListener, OnCancelListener {
	
	public static final String ACTION_BRICKS_LIST_CHANGED = "at.tugraz.ist.catroid.BRICKS_LIST_CHANGED";
	public static final String ACTION_COSTUME_DELETED = "at.tugraz.ist.catroid.COSTUME_DELETED";
	public static final String ACTION_SOUND_DELETED = "at.tugraz.ist.catroid.SOUND_DELETED";
	
	public static final int INDEX_TAB_SCRIPTS = 0;
	public static final int INDEX_TAB_COSTUMES = 1;
	public static final int INDEX_TAB_SOUNDS = 2;
	
	private ActionBar actionBar;
	private ViewPager viewPager;
	private TabsPagerAdapter tabsAdapter;
	
	private TabHost tabHost;
	private boolean addScript;
	private boolean isCanceled;
	public SoundInfo selectedSoundInfo;
	private RenameSoundDialog renameSoundDialog;
	public CostumeData selectedCostumeData;
	public int selectedPosition;
	private RenameCostumeDialog renameCostumeDialog;
	private DeleteCostumeDialog deleteCostumeDialog;
	private DeleteSoundDialog deleteSoundDialog;
	public String selectedCategory;
	public static final int DIALOG_RENAME_COSTUME = 0;
	public static final int DIALOG_RENAME_SOUND = 1;
	public static final int DIALOG_BRICK_CATEGORY = 2;
	public static final int DIALOG_ADD_BRICK = 3;
	public static final int DIALOG_DELETE_COSTUME = 4;
	public static final int DIALOG_DELETE_SOUND = 5;

	private boolean dontcreateNewBrick;

	private void setupTabHost() {
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addScript = false;
		isCanceled = false;
		dontcreateNewBrick = false;

		setContentView(R.layout.activity_scripttab);
		Utils.loadProjectIfNeeded(this);

		setupTabHost();
		viewPager = (ViewPager) findViewById(R.id.pager);
		tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

		tabsAdapter = new TabsPagerAdapter(this, tabHost, viewPager);
		setupTab(R.drawable.ic_tab_scripts_selector, getString(R.string.scripts), ScriptFragment.class, null);
		
		int costumeIcon;
		String costumeLabel;

		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		if (ProjectManager.getInstance().getCurrentProject().getSpriteList().indexOf(currentSprite) == 0) {
			costumeIcon = R.drawable.ic_tab_background_selector;
			costumeLabel = this.getString(R.string.backgrounds);
		} else {
			costumeIcon = R.drawable.ic_tab_costumes_selector;
			costumeLabel = this.getString(R.string.costumes);
		}
		
		setupTab(costumeIcon, costumeLabel, CostumeFragment.class, null);
		setupTab(R.drawable.ic_tab_sounds_selector, getString(R.string.sounds), SoundFragment.class, null);

		setUpActionBar();
		if (getLastCustomNonConfigurationInstance() != null) {
			selectedCategory = (String) ((ArrayList<?>) getLastCustomNonConfigurationInstance()).get(0);
			selectedCostumeData = (CostumeData) ((ArrayList<?>) getLastCustomNonConfigurationInstance()).get(1);
			selectedSoundInfo = (SoundInfo) ((ArrayList<?>) getLastCustomNonConfigurationInstance()).get(2);
		}
	}

	@Override
	public ArrayList<Object> onRetainCustomNonConfigurationInstance() {
		ArrayList<Object> savedMember = new ArrayList<Object>();
		savedMember.add(selectedCategory);
		savedMember.add(selectedCostumeData);
		savedMember.add(selectedSoundInfo);
		return savedMember;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_scripttab, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home: {
				Intent intent = new Intent(this, MainMenuActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			}
			case R.id.menu_start: {
				Intent intent = new Intent(ScriptTabActivity.this, PreStageActivity.class);
				startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
				return true;
			}
		}

		return super.onOptionsItemSelected(item);
	}
	
	private void setUpActionBar() {
		actionBar = getSupportActionBar();

		String title = this.getResources().getString(R.string.sprite_name) + " "
				+ ProjectManager.getInstance().getCurrentSprite().getName();
		actionBar.setTitle(title);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == PreStageActivity.REQUEST_RESOURCES_INIT && resultCode == RESULT_OK) {
			Intent intent = new Intent(ScriptTabActivity.this, StageActivity.class);
			startActivity(intent);
		}
	}

	private void setupTab(Integer drawableId, final String tag, Class<?> clss, Bundle args) {
		tabsAdapter.addTab(tabHost.newTabSpec(tag).setIndicator(createTabView(drawableId, this, tag)), clss, args);
	}

	private static View createTabView(Integer id, final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.activity_tabscriptactivity_tabs, null);
		TextView tabTextView = (TextView) view.findViewById(R.id.tabsText);
		ImageView tabImageView = (ImageView) view.findViewById(R.id.tabsIcon);
		tabTextView.setText(text);
		if (id != null) {
			tabImageView.setImageResource(id);
			tabImageView.setVisibility(ImageView.VISIBLE);
			tabImageView.setTag(id);
		}
		return view;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
			case DIALOG_RENAME_SOUND:
				if (selectedSoundInfo != null) {
					renameSoundDialog = new RenameSoundDialog(this);
					dialog = renameSoundDialog.createDialog(selectedSoundInfo);
				}
				break;
			case DIALOG_RENAME_COSTUME:
				if (selectedCostumeData != null) {
					renameCostumeDialog = new RenameCostumeDialog(this);
					dialog = renameCostumeDialog.createDialog(selectedCostumeData);
				}
				break;
			case DIALOG_BRICK_CATEGORY:
				dialog = new BrickCategoryDialog(this);
				dialog.setOnDismissListener(this);
				dialog.setOnCancelListener(this);
				break;
			case DIALOG_ADD_BRICK:
				if (selectedCategory != null) {
					dialog = new AddBrickDialog(this, selectedCategory);
				}
				break;
			case DIALOG_DELETE_COSTUME:
				if (selectedCostumeData != null) {
					deleteCostumeDialog = new DeleteCostumeDialog(this);
					dialog = deleteCostumeDialog.createDialog();
				}
				break;
			case DIALOG_DELETE_SOUND:
				if (selectedSoundInfo != null) {
					deleteSoundDialog = new DeleteSoundDialog(this);
					dialog = deleteSoundDialog.createDialog();
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
			case DIALOG_RENAME_SOUND:
				EditText soundTitleInput = (EditText) dialog.findViewById(R.id.dialog_rename_sound_editText);
				soundTitleInput.setText(selectedSoundInfo.getTitle());
				break;
			case DIALOG_RENAME_COSTUME:
				EditText costumeTitleInput = (EditText) dialog.findViewById(R.id.dialog_rename_costume_editText);
				costumeTitleInput.setText(selectedCostumeData.getCostumeName());
				break;
		}
	}

	public void handlePositiveButtonRenameSound(View v) {
		String newSoundTitle = renameSoundDialog.handleOkButton();

		if (newSoundTitle != null && !newSoundTitle.equalsIgnoreCase("")) {
			selectedSoundInfo.setTitle(newSoundTitle);
			SoundFragment fragment = (SoundFragment) getTabFragment(INDEX_TAB_SOUNDS);
			SoundAdapter adapter = (SoundAdapter) fragment.getListAdapter();
			adapter.notifyDataSetChanged();
		} else {
			Utils.displayErrorMessage(this, getString(R.string.soundname_invalid));
		}
	}
	
	public void handleNegativeButtonRenameSound(View v) {
		dismissDialog(DIALOG_RENAME_SOUND);
	}

	public void handlePositiveButtonRenameCostume(View v) {
		String newCostumeName = renameCostumeDialog.handleOkButton();

		if (newCostumeName != null && !newCostumeName.equalsIgnoreCase("")) {
			selectedCostumeData.setCostumeName(newCostumeName);
			CostumeFragment fragment = (CostumeFragment) getTabFragment(INDEX_TAB_COSTUMES);
			CostumeAdapter adapter = (CostumeAdapter) fragment.getListAdapter();
			adapter.notifyDataSetChanged();
		} else {
			Utils.displayErrorMessage(this, getString(R.string.costumename_invalid));
		}
	}

	public void handleNegativeButtonRenameCostume(View v) {
		dismissDialog(DIALOG_RENAME_COSTUME);
	}
	
	public void handlePositiveButtonDeleteCostume(View v) {
		deleteCostumeDialog.handleOkButton();
	}

	public void handleNegativeButtonDeleteCostume(View v) {
		dismissDialog(DIALOG_DELETE_COSTUME);
	}

	public void handlePositiveButtonDeleteSound(View v) {
		deleteSoundDialog.handleOkButton();
	}

	public void handleNegativeButtonDeleteSound(View v) {
		dismissDialog(DIALOG_DELETE_SOUND);
	}

	public void onDismiss(DialogInterface dialogInterface) {

		if (!dontcreateNewBrick) {
			if (!isCanceled) {
				if (addScript) {
					((ScriptFragment) getTabFragment(INDEX_TAB_SCRIPTS)).setAddNewScript();
					addScript = false;
				}

				sendBroadcast(new Intent(ACTION_BRICKS_LIST_CHANGED));
			}
			isCanceled = false;
		}
		dontcreateNewBrick = false;
	}

	public void onCancel(DialogInterface dialog) {
		isCanceled = true;
		sendBroadcast(new Intent(ACTION_BRICKS_LIST_CHANGED));
	}

	public void setNewScript() {
		addScript = true;
	}

	public void setDontcreateNewBrick() {
		dontcreateNewBrick = true;
	}
	
	public Fragment getTabFragment(int position) {
		if (position < 0 || position > 2) {
			throw new IllegalArgumentException("There is no tab Fragment with index: " + position);
		}
		
		return getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + position);
	}
	
	public Fragment getCurrentTabFragment() {
		return getTabFragment(tabHost.getCurrentTab());
	}
}
