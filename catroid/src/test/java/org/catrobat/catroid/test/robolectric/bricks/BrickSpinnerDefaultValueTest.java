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

package org.catrobat.catroid.test.robolectric.bricks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick;
import org.catrobat.catroid.content.bricks.AskBrick;
import org.catrobat.catroid.content.bricks.AskSpeechBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.CloneBrick;
import org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick;
import org.catrobat.catroid.content.bricks.HideTextBrick;
import org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick;
import org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.content.bricks.SetBackgroundAndWaitBrick;
import org.catrobat.catroid.content.bricks.SetBackgroundBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.ShowTextBrick;
import org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick;
import org.catrobat.catroid.content.bricks.WhenBounceOffBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;

import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS;
import static org.koin.java.KoinJavaComponent.inject;

@RunWith(ParameterizedRobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.P})
public class BrickSpinnerDefaultValueTest {

	private CategoryBricksFactory categoryBricksFactory;
	private Sprite sprite;
	private Activity activity;

	private final List<String> speechAISettings = new ArrayList<>(Arrays.asList(
			SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS,
			SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS));

	@ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"BroadcastReceiverBrick - R.id.brick_broadcast_spinner", "Event", BroadcastReceiverBrick.class, R.id.brick_broadcast_spinner, "new message"},
				{"BroadcastBrick - R.id.brick_broadcast_spinner", "Event", BroadcastBrick.class, R.id.brick_broadcast_spinner, "new message"},
				{"BroadcastReceiverBrick - R.id.brick_broadcast_spinner", "Event", BroadcastReceiverBrick.class, R.id.brick_broadcast_spinner, "new message"},
				{"WhenBounceOffBrick - R.id.brick_when_bounce_off_spinner", "Event", WhenBounceOffBrick.class, R.id.brick_when_bounce_off_spinner, "\0any edge, actor, or object\0"},
				{"WhenBackgroundChangesBrick - R.id.brick_when_background_spinner", "Event", WhenBackgroundChangesBrick.class, R.id.brick_when_background_spinner, "new…"},
				{"PlaySoundBrick - R.id.brick_play_sound_spinner", "Sound", PlaySoundBrick.class, R.id.brick_play_sound_spinner, "new…"},
				{"PlaySoundAndWaitBrick - R.id.brick_play_sound_spinner", "Sound", PlaySoundAndWaitBrick.class, R.id.brick_play_sound_spinner, "new…"},
				{"AskSpeechBrick - R.id.brick_ask_speech_spinner", "Sound", AskSpeechBrick.class, R.id.brick_ask_speech_spinner, "new…"},
				{"SetLookBrick - R.id.brick_set_look_spinner", "Looks", SetLookBrick.class, R.id.brick_set_look_spinner, "new…"},
				{"AskBrick - R.id.brick_ask_spinner", "Looks", AskBrick.class, R.id.brick_ask_spinner, "new…"},
				{"WhenBackgroundChangesBrick - R.id.brick_when_background_spinner", "Looks", WhenBackgroundChangesBrick.class, R.id.brick_when_background_spinner, "new…"},
				{"SetBackgroundBrick - R.id.brick_set_look_spinner", "Looks", SetBackgroundBrick.class, R.id.brick_set_background_spinner, "new…"},
				{"SetBackgroundAndWaitBrick - R.id.brick_set_look_spinner", "Looks", SetBackgroundAndWaitBrick.class, R.id.brick_set_background_spinner, "new…"},
				{"SetVariableBrick - R.id.set_variable_spinner", "Data", SetVariableBrick.class, R.id.set_variable_spinner, "new…"},
				{"ChangeVariableBrick - R.id.change_variable_spinner", "Data", ChangeVariableBrick.class, R.id.change_variable_spinner, "new…"},
				{"ShowTextBrick - R.id.show_variable_spinner", "Data", ShowTextBrick.class, R.id.show_variable_spinner, "new…"},
				{"HideTextBrick - R.id.hide_variable_spinner", "Data", HideTextBrick.class, R.id.hide_variable_spinner, "new…"},
				{"AddItemToUserListBrick - R.id.add_item_to_userlist_spinner", "Data", AddItemToUserListBrick.class, R.id.add_item_to_userlist_spinner, "new…"},
				{"DeleteItemOfUserListBrick - R.id.delete_item_of_userlist_spinner", "Data", DeleteItemOfUserListBrick.class, R.id.delete_item_of_userlist_spinner, "new…"},
				{"InsertItemIntoUserListBrick - R.id.insert_item_into_userlist_spinner", "Data", InsertItemIntoUserListBrick.class, R.id.insert_item_into_userlist_spinner, "new…"},
				{"ReplaceItemInUserListBrick - R.id.replace_item_in_userlist_spinner", "Data", ReplaceItemInUserListBrick.class, R.id.replace_item_in_userlist_spinner, "new…"},
				{"SceneTransitionBrick - R.id.brick_scene_transition_spinner", "Control", SceneTransitionBrick.class, R.id.brick_scene_transition_spinner, "new…"},
				{"SceneStartBrick - R.id.brick_scene_start_spinner", "Control", SceneStartBrick.class, R.id.brick_scene_start_spinner, "Scene"},
				{"CloneBrick - R.id.brick_clone_spinner", "Control", CloneBrick.class, R.id.brick_clone_spinner, "yourself"},
		});
	}

	@SuppressWarnings("PMD.UnusedPrivateField")
	public String name;

	public String category;

	public Class brickClazz;

	public int spinnerId;

	public String expected;

	public BrickSpinnerDefaultValueTest(String name, String category, Class brickClazz, int formulaTextFieldId, String expected) {
		this.name = name;
		this.category = category;
		this.brickClazz = brickClazz;
		this.spinnerId = formulaTextFieldId;
		this.expected = expected;
	}

	@Before
	public void setUp() throws Exception {
		ActivityController<SpriteActivity> activityController = Robolectric.buildActivity(SpriteActivity.class);
		activity = activityController.get();

		SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager
				.getDefaultSharedPreferences(activity.getApplicationContext()).edit();
		for (String setting : speechAISettings) {
			sharedPreferencesEditor.putBoolean(setting, true);
		}
		sharedPreferencesEditor.commit();

		createProject(activity);
		activityController.create().resume();
		categoryBricksFactory = new CategoryBricksFactory();
	}

	@After
	public void tearDown() {
		SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager
				.getDefaultSharedPreferences(activity.getApplicationContext()).edit();
		sharedPreferencesEditor.clear().commit();
		final ProjectManager projectManager = inject(ProjectManager.class).getValue();
		projectManager.resetProjectManager();
	}

	public void createProject(Context context) {
		Project project = new Project(context, getClass().getSimpleName());
		sprite = new Sprite("testSprite");
		Script script = new StartScript();
		script.addBrick(new SetXBrick());
		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);
		final ProjectManager projectManager = inject(ProjectManager.class).getValue();
		projectManager.setCurrentProject(project);
		projectManager.setCurrentSprite(sprite);
		projectManager.setCurrentlyEditedScene(project.getDefaultScene());
	}

	private Brick getBrickFromCategoryBricksFactory() {
		List<Brick> categoryBricks = categoryBricksFactory.getBricks(category, false, activity);

		Brick brickInAdapter = null;
		for (Brick brick : categoryBricks) {
			if (brickClazz.isInstance(brick)) {
				brickInAdapter = brick;
				break;
			}
		}
		assertNotNull(brickInAdapter);
		return brickInAdapter;
	}

	@Test
	public void testDefaultSpinnerSelection() {
		Brick brick = getBrickFromCategoryBricksFactory();
		View brickView = brick.getView(activity);
		assertNotNull(brickView);

		Spinner brickSpinner = brickView.findViewById(spinnerId);
		assertNotNull(brickSpinner);

		assertEquals(expected, ((Nameable) brickSpinner.getSelectedItem()).getName());
	}
}
