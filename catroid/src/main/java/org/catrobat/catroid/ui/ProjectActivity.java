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

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.cast.CastManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.drone.ardrone.DroneServiceWrapper;
import org.catrobat.catroid.drone.ardrone.DroneStageActivity;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.transfers.GetFacebookUserInfoTask;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.dialogs.LegoEV3SensorConfigInfoDialog;
import org.catrobat.catroid.ui.dialogs.LegoNXTSensorConfigInfoDialog;
import org.catrobat.catroid.ui.dialogs.MergeSceneDialog;
import org.catrobat.catroid.ui.dialogs.NewSceneDialog;
import org.catrobat.catroid.ui.dialogs.NewSpriteDialog;
import org.catrobat.catroid.ui.dialogs.PlaySceneDialog;
import org.catrobat.catroid.ui.dialogs.SignInDialog;
import org.catrobat.catroid.ui.fragment.ListItemActionsInterface;
import org.catrobat.catroid.ui.fragment.SceneListFragment;
import org.catrobat.catroid.ui.fragment.SpritesListFragment;
import org.catrobat.catroid.utils.IconsUtil;
import org.catrobat.catroid.utils.TextSizeUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.util.concurrent.locks.Lock;

public class ProjectActivity extends BaseCastActivity {

	private static final String TAG = ProjectActivity.class.getSimpleName();

	public static final int FRAGMENT_SPRITES = 0;
	public static final int FRAGMENT_SCENES = 1;

	public static final String EXTRA_FRAGMENT_POSITION = "org.catrobat.catroid.ui.fragmentPosition";

	private ListItemActionsInterface actionListener;

	private Fragment currentFragment;
	private SpritesListFragment spritesListFragment;
	private SceneListFragment sceneListFragment;
	private static int currentFragmentPosition;
	private FragmentManager fragmentManager = getFragmentManager();
	private String currentFragmentTag;

	private Lock viewSwitchLock = new ViewSwitchLock();
	private CallbackManager callbackManager;
	private SignInDialog signInDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initializeFacebookSdk();

		setContentView(R.layout.activity_project);

		IconsUtil.setBottomBarIconSize(getApplicationContext(), this.findViewById(android.R.id.content));

		currentFragmentPosition = FRAGMENT_SCENES;

		if (getIntent() != null && getIntent().hasExtra(Constants.PROJECT_OPENED_FROM_PROJECTS_LIST)) {
			setReturnToProjectsList(true);
		}

		if (savedInstanceState == null) {
			Bundle bundle = this.getIntent().getExtras();

			if (bundle != null) {
				currentFragmentPosition = bundle.getInt(EXTRA_FRAGMENT_POSITION, FRAGMENT_SCENES);
			}
		}

		Project project = ProjectManager.getInstance().getCurrentProject();
		if (project == null || project.getSceneList() == null || project.getSceneList().size() == 1) {
			currentFragmentPosition = FRAGMENT_SPRITES;
		}

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		updateCurrentFragment(currentFragmentPosition, fragmentTransaction);
		fragmentTransaction.commit();

		showLegoInfoFragmentIfNeeded(this.getFragmentManager());

		TextSizeUtil.enlargeViewGroup((ViewGroup) getWindow().getDecorView().getRootView());
	}

	@Override
	protected void onStart() {
		super.onStart();
		BaseSettingsActivity.setLegoMindstormsNXTSensorChooserEnabled(this, true);
		BaseSettingsActivity.setLegoMindstormsEV3SensorChooserEnabled(this, true);
		BaseSettingsActivity.setDroneChooserEnabled(this, true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setActionBarTitle();
	}

	private void setActionBarTitle() {

		String programName = getString(R.string.app_name);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			programName = bundle.getString(Constants.PROJECTNAME_TO_LOAD);
		} else {
			Project project = ProjectManager.getInstance().getCurrentProject();
			if (project != null) {
				programName = project.getName();
			}
		}

		final ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setHomeButtonEnabled(true);
			actionBar.setTitle(programName);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			bundle.remove(Constants.PROJECTNAME_TO_LOAD);
		}
	}

	private void updateCurrentFragment(int fragmentPosition, FragmentTransaction fragmentTransaction) {
		boolean fragmentExists = true;
		currentFragmentPosition = fragmentPosition;

		switch (currentFragmentPosition) {
			case FRAGMENT_SCENES:
				if (sceneListFragment == null) {
					sceneListFragment = new SceneListFragment();
					fragmentExists = false;
				}
				currentFragmentTag = SceneListFragment.TAG;
				currentFragment = sceneListFragment;
				actionListener = sceneListFragment;
				break;
			case FRAGMENT_SPRITES:
				if (spritesListFragment == null) {
					spritesListFragment = new SpritesListFragment();
					fragmentExists = false;
				}
				currentFragmentTag = SpritesListFragment.TAG;
				currentFragment = spritesListFragment;
				actionListener = spritesListFragment;
				break;
		}

		if (fragmentExists) {
			fragmentTransaction.show(currentFragment);
		} else {
			fragmentTransaction.add(R.id.fragment_container, currentFragment, currentFragmentTag);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (currentFragmentPosition == FRAGMENT_SPRITES && spritesListFragment != null) {
			handleShowDetails(spritesListFragment.getShowDetails(), menu.findItem(R.id.show_details));
		} else {
			menu.findItem(R.id.groups_create).setVisible(false);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (currentFragment != null) {
			getMenuInflater().inflate(R.menu.menu_current_project, menu);

			if (currentFragmentPosition == FRAGMENT_SCENES) {
				menu.findItem(R.id.show_details).setVisible(false);
				menu.findItem(R.id.backpack).setVisible(true);
				menu.findItem(R.id.merge_scene).setVisible(true);
			} else {
				menu.findItem(R.id.backpack).setVisible(true);
			}
		}
		TextSizeUtil.enlargeOptionsMenu(menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;

			case R.id.show_details:
				handleShowDetails(!spritesListFragment.getShowDetails(), item);
				break;

			case R.id.backpack:
				showBackPackChooser();
				break;

			case R.id.copy:
				actionListener.startCopyActionMode();
				break;

			case R.id.cut:
				break;

			case R.id.insert_below:
				break;

			case R.id.move:
				break;

			case R.id.rename:
				actionListener.startRenameActionMode();
				break;

			case R.id.delete:
				actionListener.startDeleteActionMode();
				break;

			case R.id.upload:
				ProjectManager.getInstance().uploadProject(Utils.getCurrentProjectName(this), this);
				break;

			case R.id.groups_create:
				spritesListFragment.showNewObjectGroupDialog();
				break;

			case R.id.new_scene:
				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				Fragment previousFragment = getFragmentManager().findFragmentByTag(NewSceneDialog.DIALOG_FRAGMENT_TAG);
				if (previousFragment != null) {
					fragmentTransaction.remove(previousFragment);
				}

				boolean fromSpriteOverview = currentFragmentPosition == FRAGMENT_SPRITES;
				fromSpriteOverview &= ProjectManager.getInstance().getCurrentProject().getSceneList().size() > 1;
				NewSceneDialog newSceneFragment = new NewSceneDialog(false, fromSpriteOverview);
				newSceneFragment.show(fragmentTransaction, NewSceneDialog.DIALOG_FRAGMENT_TAG);
				break;
			case R.id.merge_scene:
				fragmentTransaction = getFragmentManager().beginTransaction();
				previousFragment = getFragmentManager().findFragmentByTag(MergeSceneDialog.DIALOG_FRAGMENT_TAG);
				if (previousFragment != null) {
					fragmentTransaction.remove(previousFragment);
				}

				MergeSceneDialog mergeSceneDialog = new MergeSceneDialog();
				mergeSceneDialog.show(fragmentTransaction, MergeSceneDialog.DIALOG_FRAGMENT_TAG);
				break;
		}
		SpannableString spanString = new SpannableString(item.getTitle().toString());
		int end = spanString.length();
		spanString.setSpan(new RelativeSizeSpan(1.5f), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return super.onOptionsItemSelected(item);
	}

	private void showBackPackChooser() {
		updateFragmentPosition();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		CharSequence[] items;
		int numberOfItemsInBackpack = 0;
		switch (currentFragmentPosition) {
			case FRAGMENT_SPRITES:
				numberOfItemsInBackpack = BackPackListManager.getInstance().getBackPackedSprites().size();
				break;
			case FRAGMENT_SCENES:
				numberOfItemsInBackpack = BackPackListManager.getInstance().getBackPackedScenes().size();
				break;
		}

		if (numberOfItemsInBackpack == 0) {
			actionListener.startBackPackActionMode();
		} else {
			items = new CharSequence[] {getString(R.string.packing), getString(R.string.unpack)};
			builder.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == 0) {
						actionListener.startBackPackActionMode();
					} else if (which == 1) {
						openBackPack();
					}
					dialog.dismiss();
				}
			});
			builder.setTitle(R.string.backpack_title);
			builder.setCancelable(true);

			final AlertDialog alertDialog = builder.create();
			alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
				@Override
				public void onShow(DialogInterface dialog) {
					TextSizeUtil.enlargeViewGroup((ViewGroup) alertDialog.getWindow().getDecorView().getRootView());
				}
			});
			alertDialog.show();
		}
	}

	private void openBackPack() {
		updateFragmentPosition();
		Intent intent = new Intent(this, BackPackActivity.class);
		int fragmentPos = 0;
		switch (currentFragmentPosition) {
			case FRAGMENT_SCENES:
				fragmentPos = BackPackActivity.FRAGMENT_BACKPACK_SCENES;
				break;
			case FRAGMENT_SPRITES:
				fragmentPos = BackPackActivity.FRAGMENT_BACKPACK_SPRITES;
				break;
		}
		intent.putExtra(BackPackActivity.EXTRA_FRAGMENT_POSITION, fragmentPos);
		startActivity(intent);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		callbackManager.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PreStageActivity.REQUEST_RESOURCES_INIT && resultCode == RESULT_OK) {
			Intent intent;
			if (data != null) {
				if (DroneServiceWrapper.checkARDroneAvailability()) {
					intent = new Intent(ProjectActivity.this, DroneStageActivity.class);
				} else {
					intent = new Intent(ProjectActivity.this, StageActivity.class);
				}
				startActivity(intent);
			}
		}
		if (requestCode == StageActivity.STAGE_ACTIVITY_FINISH) {
			SensorHandler.stopSensorListeners();
			FaceDetectionHandler.stopFaceDetection();
		}

		if (requestCode != RESULT_OK && BaseSettingsActivity.isCastSharedPreferenceEnabled(this)
				&& ProjectManager.getInstance().getCurrentProject().isCastProject()
				&& !CastManager.getInstance().isConnected()) {

			CastManager.getInstance().openDeviceSelectorOrDisconnectDialog(this);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			sendBroadcast(new Intent(ScriptActivity.ACTION_SPRITES_LIST_INIT));
		}
	}

	public void handleAddButton(View view) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}

		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		Fragment previousFragment;

		previousFragment = getFragmentManager().findFragmentByTag(NewSpriteDialog.DIALOG_FRAGMENT_TAG);
		if (previousFragment != null) {
			fragmentTransaction.remove(previousFragment);
		}

		updateFragmentPosition();
		switch (currentFragmentPosition) {
			case FRAGMENT_SCENES:
				NewSceneDialog newSceneFragment = new NewSceneDialog(false, false);
				newSceneFragment.show(fragmentTransaction, NewSpriteDialog.DIALOG_FRAGMENT_TAG);
				break;
			case FRAGMENT_SPRITES:
				NewSpriteDialog newSpriteFragment = new NewSpriteDialog();
				newSpriteFragment.show(fragmentTransaction, NewSpriteDialog.DIALOG_FRAGMENT_TAG);
				break;
		}
	}

	public void handlePlayButton(View view) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		Scene currentScene = ProjectManager.getInstance().getCurrentScene();

		updateFragmentPosition();

		switch (currentFragmentPosition) {
			case FRAGMENT_SCENES:
				ProjectManager.getInstance().setSceneToPlay(currentProject.getDefaultScene());
				ProjectManager.getInstance().setStartScene(currentProject.getDefaultScene());
				startPreStageActivity();
				break;
			case FRAGMENT_SPRITES:
				if (currentScene.getName().equals(currentProject.getDefaultScene().getName())) {
					ProjectManager.getInstance().setSceneToPlay(currentScene);
					ProjectManager.getInstance().setStartScene(currentScene);
					startPreStageActivity();
					return;
				}
				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				Fragment previousFragment = getFragmentManager().findFragmentByTag(NewSceneDialog.DIALOG_FRAGMENT_TAG);
				if (previousFragment != null) {
					fragmentTransaction.remove(previousFragment);
				}

				PlaySceneDialog playSceneDialog = new PlaySceneDialog();
				playSceneDialog.show(fragmentTransaction, PlaySceneDialog.DIALOG_FRAGMENT_TAG);
				break;
		}
	}

	public void startPreStageActivity() {
		Intent intent = new Intent(this, PreStageActivity.class);
		startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// Dismiss ActionMode without effecting sounds
		if (actionListener.getActionModeActive() && event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_UP) {
			updateFragmentPosition();
			switch (currentFragmentPosition) {
				case FRAGMENT_SCENES:
					sceneListFragment.clearCheckedItems();
					break;
				case FRAGMENT_SPRITES:
					spritesListFragment.getSpriteAdapter().clearCheckedItems();
					break;
			}
		}

		return super.dispatchKeyEvent(event);
	}

	public void handleShowDetails(boolean showDetails, MenuItem item) {
		spritesListFragment.setShowDetails(showDetails);

		item.setTitle(showDetails ? R.string.hide_details : R.string.show_details);
		TextSizeUtil.enlargeOptionsItem(item);
	}

	public void showEmptyActionModeDialog(String actionMode) {
		@SuppressLint("InflateParams")
		View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_action_mode_empty, null);
		TextView actionModeEmptyText = (TextView) dialogView.findViewById(R.id.dialog_action_mode_emtpy_text);
		actionModeEmptyText.setTextSize(TypedValue.COMPLEX_UNIT_PX, actionModeEmptyText.getTextSize() * TextSizeUtil.getModifier());

		if (actionMode.equals(getString(R.string.backpack))) {
			actionModeEmptyText.setText(getString(R.string.nothing_to_backpack_and_unpack));
		} else if (actionMode.equals(getString(R.string.unpack))) {
			actionModeEmptyText.setText(getString(R.string.nothing_to_unpack));
		} else if (actionMode.equals(getString(R.string.delete))) {
			actionModeEmptyText.setText(getString(R.string.nothing_to_delete));
		} else if (actionMode.equals(getString(R.string.copy))) {
			actionModeEmptyText.setText(getString(R.string.nothing_to_copy));
		} else if (actionMode.equals(getString(R.string.rename))) {
			actionModeEmptyText.setText(getString(R.string.nothing_to_rename));
		}

		final AlertDialog actionModeEmptyDialog = new AlertDialog.Builder(this).setView(dialogView)
				.setTitle(actionMode)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();

		actionModeEmptyDialog.setCanceledOnTouchOutside(true);
		actionModeEmptyDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		actionModeEmptyDialog.show();

		actionModeEmptyDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				TextSizeUtil.enlargeViewGroup((ViewGroup) actionModeEmptyDialog.getWindow().getDecorView().getRootView());
			}
		});
	}

	public SpritesListFragment getSpritesListFragment() {
		return spritesListFragment;
	}

	public SceneListFragment getSceneListFragment() {
		return sceneListFragment;
	}

	public void initializeFacebookSdk() {
		FacebookSdk.sdkInitialize(getApplicationContext());
		callbackManager = CallbackManager.Factory.create();

		LoginManager.getInstance().registerCallback(callbackManager,
				new FacebookCallback<LoginResult>() {
					@Override
					public void onSuccess(LoginResult loginResult) {
						Log.d(TAG, loginResult.toString());
						AccessToken accessToken = loginResult.getAccessToken();
						GetFacebookUserInfoTask getFacebookUserInfoTask = new GetFacebookUserInfoTask(ProjectActivity.this,
								accessToken.getToken(), accessToken.getUserId());
						getFacebookUserInfoTask.setOnGetFacebookUserInfoTaskCompleteListener(signInDialog);
						getFacebookUserInfoTask.execute();
					}

					@Override
					public void onCancel() {
						Log.d(TAG, "cancel");
					}

					@Override
					public void onError(FacebookException exception) {
						ToastUtil.showError(ProjectActivity.this, exception.getMessage());
						Log.d(TAG, exception.getMessage());
					}
				});
	}

	public void setSignInDialog(SignInDialog signInDialog) {
		this.signInDialog = signInDialog;
	}

	private void updateFragmentPosition() {
		//TODO: Just a quickfix, we need to investigate why the position is sometimes not correct
		if (currentFragment instanceof SceneListFragment) {
			currentFragmentPosition = FRAGMENT_SCENES;
		} else if (currentFragment instanceof SpritesListFragment) {
			currentFragmentPosition = FRAGMENT_SPRITES;
		}
	}

	private boolean needToShowLegoEV3InfoDialog() {
		boolean isLegoEV3InfoDialogDisabled = BaseSettingsActivity
				.getShowLegoEV3MindstormsSensorInfoDialog(getApplicationContext());

		boolean legoEV3ResourcesRequired = (ProjectManager.getInstance().getCurrentProject().getRequiredResources()
				& Brick.BLUETOOTH_LEGO_EV3) != 0;

		boolean dialogAlreadyShown = !ProjectManager.getInstance().getShowLegoSensorInfoDialog();

		return !isLegoEV3InfoDialogDisabled && legoEV3ResourcesRequired && !dialogAlreadyShown;
	}

	private boolean needToShowLegoNXTInfoDialog() {
		boolean isLegoNXTInfoDialogDisabled = BaseSettingsActivity
				.getShowLegoNXTMindstormsSensorInfoDialog(getApplicationContext());

		boolean legoNXTResourcesRequired = (ProjectManager.getInstance().getCurrentProject().getRequiredResources()
				& Brick.BLUETOOTH_LEGO_NXT) != 0;

		boolean dialogAlreadyShown = !ProjectManager.getInstance().getShowLegoSensorInfoDialog();

		return !isLegoNXTInfoDialogDisabled && legoNXTResourcesRequired && !dialogAlreadyShown;
	}

	private void showLegoInfoFragmentIfNeeded(FragmentManager fragmentManager) {

		if (needToShowLegoNXTInfoDialog()) {
			DialogFragment dialog = new LegoNXTSensorConfigInfoDialog();
			dialog.show(fragmentManager, LegoNXTSensorConfigInfoDialog.DIALOG_FRAGMENT_TAG);
		}
		if (needToShowLegoEV3InfoDialog()) {
			DialogFragment dialog = new LegoEV3SensorConfigInfoDialog();
			dialog.show(fragmentManager, LegoEV3SensorConfigInfoDialog.DIALOG_FRAGMENT_TAG);
		}
		ProjectManager.getInstance().setShowLegoSensorInfoDialog(false);
	}
}
