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
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scripttab);

		setupTabHost();
		tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

		Intent intent; // Reusable Intent for each tab

		intent = new Intent().setClass(this, ScriptActivity.class);
		setupTab(R.drawable.ic_tab_scripts, this.getString(R.string.scripts), intent);
		intent = new Intent().setClass(this, CostumeActivity.class);
		setupTab(R.drawable.ic_tab_costumes, this.getString(R.string.costumes), intent);
		intent = new Intent().setClass(this, SoundActivity.class);
		setupTab(R.drawable.ic_tab_sounds, this.getString(R.string.sounds), intent);

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

	private void setupTab(Integer drawableId, final String tag, Intent intent) {
		View tabview = createTabView(drawableId, tabHost.getContext(), tag);

		TabSpec setContent = tabHost.newTabSpec(tag).setIndicator(tabview).setContent(intent);
		tabHost.addTab(setContent);

	}

	private static View createTabView(Integer id, final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.activity_tabscriptactivity_tabs, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		if (id != null) {
			tv.setCompoundDrawablesWithIntrinsicBounds(id, 0, 0, 0);
		}
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

	public static class FlingableTabHost extends TabHost {
		GestureDetector mGestureDetector;

		Animation mRightInAnimation;
		Animation mRightOutAnimation;
		Animation mLeftInAnimation;
		Animation mLeftOutAnimation;

		public FlingableTabHost(Context context, AttributeSet attrs) {
			super(context, attrs);

			mRightInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_right_in);
			mRightOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_right_out);
			mLeftInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_left_in);
			mLeftOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_left_out);

			final int minScaledFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity() * 10; // 10 = fudge by experimentation

			mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
					Log.e("fling", "on fling!!!!!!!");
					int tabCount = getTabWidget().getTabCount();
					int currentTab = getCurrentTab();
					if (Math.abs(velocityX) > minScaledFlingVelocity && Math.abs(velocityY) < minScaledFlingVelocity) {

						final boolean right = velocityX < 0;
						final int newTab = constrain(currentTab + (right ? 1 : -1), 0, tabCount - 1);
						if (newTab != currentTab) {
							// Somewhat hacky, depends on current implementation of TabHost:
							// http://android.git.kernel.org/?p=platform/frameworks/base.git;a=blob;
							// f=core/java/android/widget/TabHost.java
							View currentView = getCurrentView();
							setCurrentTab(newTab);
							View newView = getCurrentView();

							newView.startAnimation(right ? mRightInAnimation : mLeftInAnimation);
							currentView.startAnimation(right ? mRightOutAnimation : mLeftOutAnimation);
						}
					}
					return super.onFling(e1, e2, velocityX, velocityY);
				}
			});
		}

		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			Log.e("fling", "on fling intercept!!!!!!!");
			if (mGestureDetector.onTouchEvent(ev)) {
				return true;
			}
			return super.onInterceptTouchEvent(ev);
		}
	}

	//	/**
	//	 * Build a {@link View} to be used as a tab indicator, setting the requested
	//	 * string resource as its label.
	//	 */
	//	private View buildIndicator(String text) {
	//		final TextView indicator = (TextView) getLayoutInflater()
	//				.inflate(R.layout.tab_indicator, getTabWidget(), false);
	//		indicator.setText(text);
	//		return indicator;
	//	}
	//
	//	public void onHomeClick(View v) {
	//		UIUtils.goHome(this);
	//	}
	//
	//	public void onSearchClick(View v) {
	//		UIUtils.goSearch(this);
	//	}

	private static int constrain(int amount, int low, int high) {
		return amount < low ? low : (amount > high ? high : amount);
	}
}
