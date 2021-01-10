/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.content.brick.app;

import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.content.messagecontainer.BroadcastMessageBrickTestUtils.createNewBroadcastMessageOnBrick;
import static org.catrobat.catroid.uiespresso.content.messagecontainer.BroadcastMessageBrickTestUtils.editBroadcastMessageOnBrick;

@RunWith(AndroidJUnit4.class)
public class BroadcastBrickMessageUpdateTest {
	private String defaultMessage = "defaultMessage";
	private String editedMessage = "editedMessage";
	private String message = "newAddedMessage";

	private Scene secondScene;
	private Sprite secondSprite;
	private BroadcastReceiverBrick firstBroadcastBrick;

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@After
	public void tearDown() throws IOException {
		baseActivityTestRule.finishActivity();
		TestUtils.deleteProjects(BroadcastBrickMessageUpdateTest.class.getSimpleName());
	}

	@Before
	public void setUp() throws Exception {
		createTestProjectWithBricks(BroadcastBrickMessageUpdateTest.class.getSimpleName());
		baseActivityTestRule.launchActivity();
	}

	@Test
	public void testAllBroadcastBrickSpinnersContainTheNewAddedMessage() {
		createNewBroadcastMessageOnBrick(message, firstBroadcastBrick,
				baseActivityTestRule.getActivity());

		List<String> spinnerValues = Arrays.asList(
				UiTestUtils.getResourcesString(R.string.new_option),
				UiTestUtils.getResourcesString(R.string.edit_option),
				defaultMessage,
				message);

		checkAllBrickSpinnerValues(spinnerValues);
	}

	@Test
	public void testAllBroadcastBrickSpinnersContainTheEditedMessage() {
		editBroadcastMessageOnBrick(defaultMessage, editedMessage, firstBroadcastBrick,
				baseActivityTestRule.getActivity());

		List<String> spinnerValues = Arrays.asList(
				UiTestUtils.getResourcesString(R.string.new_option),
				UiTestUtils.getResourcesString(R.string.edit_option),
				editedMessage);

		checkAllBrickSpinnerValues(spinnerValues);
	}

	@Test
	public void testAllBroadcastBrickSpinnersShowTheEditedMessage() {
		editBroadcastMessageOnBrick(defaultMessage, editedMessage, firstBroadcastBrick,
				baseActivityTestRule.getActivity());

		checkShowsCorrectSpinnerMessage(editedMessage);
	}

	@Test
	public void testEditingOccursOnlyInCurrentScene() {
		editBroadcastMessageOnBrick(defaultMessage, editedMessage, firstBroadcastBrick,
				baseActivityTestRule.getActivity());

		switchScene();

		checkShowsCorrectSpinnerMessage(defaultMessage);
	}

	private void checkAllBrickSpinnerValues(List<String> spinnerValues) {
		onBrickAtPosition(1)
				.onSpinner(R.id.brick_broadcast_spinner)
				.checkNameableValuesAvailable(spinnerValues);
		onBrickAtPosition(2)
				.onSpinner(R.id.brick_broadcast_spinner)
				.checkNameableValuesAvailable(spinnerValues);
		onBrickAtPosition(3)
				.onSpinner(R.id.brick_broadcast_spinner)
				.checkNameableValuesAvailable(spinnerValues);
		onBrickAtPosition(4)
				.onSpinner(R.id.brick_broadcast_spinner)
				.checkNameableValuesAvailable(spinnerValues);
		onBrickAtPosition(5)
				.onSpinner(R.id.brick_broadcast_spinner)
				.checkNameableValuesAvailable(spinnerValues);
	}

	private void checkShowsCorrectSpinnerMessage(String message) {
		onBrickAtPosition(1)
				.onSpinner(R.id.brick_broadcast_spinner)
				.checkShowsText(message);
		onBrickAtPosition(2)
				.onSpinner(R.id.brick_broadcast_spinner)
				.checkShowsText(message);
		onBrickAtPosition(3)
				.onSpinner(R.id.brick_broadcast_spinner)
				.checkShowsText(message);
		onBrickAtPosition(4)
				.onSpinner(R.id.brick_broadcast_spinner)
				.checkShowsText(message);
		onBrickAtPosition(5)
				.onSpinner(R.id.brick_broadcast_spinner)
				.checkShowsText(message);
	}

	private void switchScene() {
		baseActivityTestRule.finishActivity();

		ProjectManager.getInstance().setCurrentSprite(secondSprite);
		ProjectManager.getInstance().setCurrentlyEditedScene(secondScene);
		baseActivityTestRule.launchActivity();
	}

	private void createTestProjectWithBricks(String projectName) {
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		Sprite firstSprite = new Sprite("spriteScene1");
		secondSprite = new Sprite("spriteScene2");

		Script script = new BroadcastScript(defaultMessage);
		firstBroadcastBrick = (BroadcastReceiverBrick) script.getScriptBrick();

		script.addBrick(new BroadcastBrick(defaultMessage));
		script.addBrick(new BroadcastWaitBrick(defaultMessage));

		firstSprite.addScript(script);

		try {
			firstSprite.addScript(script.clone());
			secondSprite.addScript(script.clone());
			secondSprite.addScript(script.clone());
		} catch (CloneNotSupportedException e) {
			Log.e(BroadcastBrickMessageUpdateTest.class.getSimpleName(), e.getMessage());
		}

		Scene firstScene = new Scene("Scene1", project);
		secondScene = new Scene("Scene2", project);

		firstScene.addSprite(firstSprite);
		secondScene.addSprite(secondSprite);

		project.addScene(firstScene);
		project.addScene(secondScene);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
		ProjectManager.getInstance().setCurrentlyEditedScene(firstScene);
	}
}
