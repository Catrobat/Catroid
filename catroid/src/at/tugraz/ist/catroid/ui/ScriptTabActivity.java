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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.stage.PreStageActivity;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.adapter.BrickAdapter;
import at.tugraz.ist.catroid.ui.adapter.TabsPagerAdapter;
import at.tugraz.ist.catroid.ui.dragndrop.DragAndDropListView;
import at.tugraz.ist.catroid.ui.fragment.CostumeFragment;
import at.tugraz.ist.catroid.ui.fragment.ScriptFragment;
import at.tugraz.ist.catroid.ui.fragment.SoundFragment;
import at.tugraz.ist.catroid.utils.Utils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ScriptTabActivity extends SherlockFragmentActivity {

	public static final String ACTION_SPRITE_RENAMED = "at.tugraz.ist.catroid.SPRITE_RENAMED";
	public static final String ACTION_SPRITES_LIST_CHANGED = "at.tugraz.ist.catroid.SPRITES_LIST_CHANGED";
	public static final String ACTION_SPRITES_LIST_INIT = "at.tugraz.ist.catroid.SPRITES_LIST_INIT";
	public static final String ACTION_NEW_BRICK_ADDED = "at.tugraz.ist.catroid.NEW_BRICK_ADDED";
	public static final String ACTION_BRICK_LIST_CHANGED = "at.tugraz.ist.catroid.BRICK_LIST_CHANGED";
	public static final String ACTION_COSTUME_DELETED = "at.tugraz.ist.catroid.COSTUME_DELETED";
	public static final String ACTION_COSTUME_RENAMED = "at.tugraz.ist.catroid.COSTUME_RENAMED";
	public static final String ACTION_SOUND_DELETED = "at.tugraz.ist.catroid.SOUND_DELETED";
	public static final String ACTION_SOUND_RENAMED = "at.tugraz.ist.catroid.SOUND_RENAMED";

	public static final int INDEX_TAB_SCRIPTS = 0;
	public static final int INDEX_TAB_COSTUMES = 1;
	public static final int INDEX_TAB_SOUNDS = 2;

	private ActionBar actionBar;
	private ViewPager viewPager;
	private TabsPagerAdapter tabsAdapter;

	private TabHost tabHost;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_scripttab);
		Utils.loadProjectIfNeeded(this);

		setUpActionBar();

		setupTabHost();
		viewPager = (ViewPager) findViewById(R.id.pager);
		tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

		tabsAdapter = new TabsPagerAdapter(this, tabHost, viewPager);
		setupTab(R.drawable.ic_tab_scripts_selector, getString(R.string.scripts), ScriptFragment.class, null);

		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		if (ProjectManager.getInstance().getCurrentProject().getSpriteList().indexOf(currentSprite) == 0) {
			setupTab(R.drawable.ic_tab_background_selector, getString(R.string.backgrounds), CostumeFragment.class,
					null);
		} else {
			setupTab(R.drawable.ic_tab_costumes_selector, getString(R.string.costumes), CostumeFragment.class, null);
		}

		setupTab(R.drawable.ic_tab_sounds_selector, getString(R.string.sounds), SoundFragment.class, null);
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PreStageActivity.REQUEST_RESOURCES_INIT && resultCode == RESULT_OK) {
			Intent intent = new Intent(ScriptTabActivity.this, StageActivity.class);
			startActivityForResult(intent, StageActivity.STAGE_ACTIVITY_FINISH);
		}
		if (requestCode == StageActivity.STAGE_ACTIVITY_FINISH) {
			ProjectManager projectManager = ProjectManager.getInstance();
			int currentSpritePos = projectManager.getCurrentSpritePosition();
			int currentScriptPos = projectManager.getCurrentScriptPosition();
			projectManager.loadProject(projectManager.getCurrentProject().getName(), this, false);
			projectManager.setCurrentSpriteWithPosition(currentSpritePos);
			projectManager.setCurrentScriptWithPosition(currentScriptPos);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (getCurrentTabFragment() instanceof ScriptFragment) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				ScriptFragment scriptFragment = (ScriptFragment) getCurrentTabFragment();
				DragAndDropListView listView = scriptFragment.getListView();
				if (listView.isCurrentlyDragging()) {
					listView.resetDraggingScreen();

					BrickAdapter adapter = scriptFragment.getAdapter();
					adapter.removeDraggedBrick();

					return true;
				}
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	private void setupTabHost() {
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
	}

	private void setUpActionBar() {
		actionBar = getSupportActionBar();

		String title = this.getResources().getString(R.string.sprite_name) + " "
				+ ProjectManager.getInstance().getCurrentSprite().getName();
		actionBar.setTitle(title);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	private void setupTab(Integer drawableId, final String tag, Class<?> clss, Bundle arguments) {
		tabsAdapter.addTab(tabHost.newTabSpec(tag).setIndicator(createTabView(drawableId, this, tag)), clss, arguments);
	}

	private static View createTabView(Integer id, final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.activity_scripttab_tabs, null);
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
