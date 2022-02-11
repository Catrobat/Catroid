/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
import android.os.Build;
import android.view.View;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenBounceOffScript;
import org.catrobat.catroid.content.WhenGamepadButtonScript;
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick;
import org.catrobat.catroid.content.bricks.AskSpeechBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.CloneBrick;
import org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick;
import org.catrobat.catroid.content.bricks.GoToBrick;
import org.catrobat.catroid.content.bricks.HideTextBrick;
import org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick;
import org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.PointToBrick;
import org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.content.bricks.SetBackgroundAndWaitBrick;
import org.catrobat.catroid.content.bricks.SetBackgroundBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetNfcTagBrick;
import org.catrobat.catroid.content.bricks.SetRotationStyleBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.ShowTextBrick;
import org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick;
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick;
import org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick;
import org.catrobat.catroid.content.bricks.WhenBounceOffBrick;
import org.catrobat.catroid.content.bricks.WhenGamepadButtonBrick;
import org.catrobat.catroid.content.bricks.WhenNfcBrick;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import static java.util.Arrays.asList;

@RunWith(ParameterizedRobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.P})
public class BrickSpinnerTest {

	private SpriteActivity activity;

	Spinner brickSpinner;

	@ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return asList(new Object[][] {
				{SceneTransitionBrick.class.getSimpleName(), new SceneTransitionBrick(""), R.id.brick_scene_transition_spinner, "Scene 2", asList("new…", "Scene 2")},
				{SceneStartBrick.class.getSimpleName(), new SceneStartBrick(""), R.id.brick_scene_start_spinner, "Scene", asList("new…", "Scene", "Scene 2")},
				{CloneBrick.class.getSimpleName(), new CloneBrick(), R.id.brick_clone_spinner, "yourself", asList("yourself", "otherTestSprite")},
				{SetNfcTagBrick.class.getSimpleName(), new SetNfcTagBrick(), R.id.brick_set_nfc_tag_ndef_record_spinner, "HTTPS", asList("Text", "HTTP", "HTTPS", "SMS", "Phone number", "E-Mail", "External type", "Empty")},
				{GoToBrick.class.getSimpleName(), new GoToBrick(), R.id.brick_go_to_spinner, "touch position", asList("touch position", "random position", "otherTestSprite")},
				{UserDefinedReceiverBrick.class.getSimpleName(), new UserDefinedReceiverBrick(), R.id.brick_set_screen_refresh_spinner, "with", asList("with", "without")},
				{PointToBrick.class.getSimpleName(), new PointToBrick(), R.id.brick_point_to_spinner, "otherTestSprite", asList("new…", "otherTestSprite")},
				{SetRotationStyleBrick.class.getSimpleName(), new SetRotationStyleBrick(), R.id.brick_set_rotation_style_spinner, "left-right only", asList("left-right only", "all-around", "don't rotate")},
				{PlaySoundBrick.class.getSimpleName(), new PlaySoundBrick(), R.id.brick_play_sound_spinner, "someSound", asList("new…", "someSound")},
				{PlaySoundAndWaitBrick.class.getSimpleName(), new PlaySoundAndWaitBrick(), R.id.brick_play_sound_spinner, "someSound", asList("new…", "someSound")},
				{AskSpeechBrick.class.getSimpleName(), new AskSpeechBrick(), R.id.brick_ask_speech_spinner, "someVariable", asList("new…", "someVariable")},
				{SetLookBrick.class.getSimpleName(), new SetLookBrick(), R.id.brick_set_look_spinner, "someLook", asList("new…", "someLook")},
				{SetBackgroundBrick.class.getSimpleName(), new SetBackgroundBrick(), R.id.brick_set_background_spinner, "someBackground", asList("new…", "someBackground")},
				{SetBackgroundAndWaitBrick.class.getSimpleName(), new SetBackgroundAndWaitBrick(), R.id.brick_set_background_spinner, "someBackground", asList("new…", "someBackground")},
				{SetVariableBrick.class.getSimpleName(), new SetVariableBrick(), R.id.set_variable_spinner, "someVariable", asList("new…", "someVariable")},
				{ChangeVariableBrick.class.getSimpleName(), new ChangeVariableBrick(), R.id.change_variable_spinner, "someVariable", asList("new…", "someVariable")},
				{ShowTextBrick.class.getSimpleName(), new ShowTextBrick(), R.id.show_variable_spinner, "someVariable", asList("new…", "someVariable")},
				{ShowTextColorSizeAlignmentBrick.class.getSimpleName() + " variable", new ShowTextColorSizeAlignmentBrick(), R.id.show_variable_color_size_spinner, "someVariable", asList("new…", "someVariable")},
				{ShowTextColorSizeAlignmentBrick.class.getSimpleName() + " alignment", new ShowTextColorSizeAlignmentBrick(), R.id.brick_show_variable_color_size_align_spinner, "centered", asList("left", "centered", "right")},
				{HideTextBrick.class.getSimpleName(), new HideTextBrick(), R.id.hide_variable_spinner, "someVariable", asList("new…", "someVariable")},
				{AddItemToUserListBrick.class.getSimpleName(), new AddItemToUserListBrick(), R.id.add_item_to_userlist_spinner, "someList", asList("new…", "someList")},
				{DeleteItemOfUserListBrick.class.getSimpleName(), new DeleteItemOfUserListBrick(), R.id.delete_item_of_userlist_spinner, "someList", asList("new…", "someList")},
				{InsertItemIntoUserListBrick.class.getSimpleName(), new InsertItemIntoUserListBrick(), R.id.insert_item_into_userlist_spinner, "someList", asList("new…", "someList")},
				{ReplaceItemInUserListBrick.class.getSimpleName(), new ReplaceItemInUserListBrick(), R.id.replace_item_in_userlist_spinner, "someList", asList("new…", "someList")},
				{BroadcastReceiverBrick.class.getSimpleName(), new BroadcastReceiverBrick(new BroadcastScript("initialMessage")), R.id.brick_broadcast_spinner, "initialMessage", asList("new…", "edit…", "initialMessage")},
				{BroadcastBrick.class.getSimpleName(), new BroadcastBrick("initialMessage"), R.id.brick_broadcast_spinner, "initialMessage", asList("new…", "edit…", "initialMessage")},
				{BroadcastWaitBrick.class.getSimpleName(), new BroadcastWaitBrick("initialMessage"), R.id.brick_broadcast_spinner, "initialMessage", asList("new…", "edit…", "initialMessage")},
				{WhenBackgroundChangesBrick.class.getSimpleName(), new WhenBackgroundChangesBrick(), R.id.brick_when_background_spinner, "someBackground", asList("new…", "someBackground")},
				{WhenBounceOffBrick.class.getSimpleName(), new WhenBounceOffBrick(new WhenBounceOffScript(null)), R.id.brick_when_bounce_off_spinner, "\0any edge, actor, or object\0", asList("\0any edge, actor, or object\0", "Background", "testSprite", "otherTestSprite")},
				{WhenNfcBrick.class.getSimpleName(), new WhenNfcBrick(), R.id.brick_when_nfc_spinner, "all", asList("Edit list of NFC tags", "all")},
				{WhenGamepadButtonBrick.class.getSimpleName(), new WhenGamepadButtonBrick(new WhenGamepadButtonScript("")), R.id.brick_when_gamepad_button_spinner, "A", asList("A", "B", "up", "down", "left", "right")},
		});
	}

	@SuppressWarnings("PMD.UnusedPrivateField")
	private String name;

	private Brick brick;

	private @IdRes int spinnerId;

	private String expectedSelection;

	private List<String> expectedContent;

	public BrickSpinnerTest(String name, Brick brick, @IdRes int spinnerId, String expectedSelection, List<String> expectedContent) {
		this.name = name;
		this.brick = brick;
		this.spinnerId = spinnerId;
		this.expectedSelection = expectedSelection;
		this.expectedContent = expectedContent;
	}

	@Before
	public void setUp() throws Exception {
		ActivityController<SpriteActivity> activityController = Robolectric.buildActivity(SpriteActivity.class);
		activity = activityController.get();
		createProject(activity);
		activityController.create().resume();

		Fragment scriptFragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		assertNotNull(scriptFragment);
		assertThat(scriptFragment, is(instanceOf(ScriptFragment.class)));

		View brickView = brick.getView(activity);
		assertNotNull(brickView);

		brickSpinner = brickView.findViewById(spinnerId);
		assertNotNull(brickSpinner);
	}

	@After
	public void tearDown() {
		ProjectManager.getInstance().resetProjectManager();
	}

	@Test
	public void spinnerDefaultSelectionTest() {
		assertEquals(expectedSelection, ((Nameable) brickSpinner.getSelectedItem()).getName());
	}

	@Test
	public void spinnerContentTest() {
		List<String> spinnerContent = new ArrayList<>();
		for (int index = 0; index < brickSpinner.getAdapter().getCount(); index++) {
			spinnerContent.add(((Nameable) brickSpinner.getAdapter().getItem(index)).getName());
		}
		assertEquals(expectedContent, spinnerContent);
	}

	public void createProject(Activity activity) {
		Project project = new Project(activity, getClass().getSimpleName());
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();
		script.addBrick(brick);
		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentlyEditedScene(project.getDefaultScene());

		Sprite sprite2 = new Sprite("otherTestSprite");
		project.getDefaultScene().addSprite(sprite2);

		Scene scene2 = new Scene("Scene 2", project);
		project.addScene(scene2);

		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setFile(Mockito.mock(File.class));
		soundInfo.setName("someSound");
		List<SoundInfo> soundInfoList = sprite.getSoundList();
		soundInfoList.add(soundInfo);

		LookData lookData = new LookData();
		lookData.setFile(Mockito.mock(File.class));
		lookData.setName("someLook");
		List<LookData> lookDataList = sprite.getLookList();
		lookDataList.add(lookData);

		LookData backgroundLookData = new LookData();
		backgroundLookData.setFile(Mockito.mock(File.class));
		backgroundLookData.setName("someBackground");
		List<LookData> backgroundLookDataList = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getBackgroundSprite().getLookList();
		backgroundLookDataList.add(backgroundLookData);

		ProjectManager.getInstance().getCurrentProject().addUserVariable(new UserVariable("someVariable"));
		ProjectManager.getInstance().getCurrentProject().addUserList(new UserList("someList"));
	}
}
