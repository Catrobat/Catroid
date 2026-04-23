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

package org.catrobat.catroid.uiespresso.content.brick.app;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.UserDefinedScript;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.UserDefinedBrick;
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.catrobat.catroid.userbrick.UserDefinedBrickInput;
import org.catrobat.catroid.userbrick.UserDefinedBrickLabel;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Collections;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DeleteUserDefinedReceiverBrickTest {

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION,
			SpriteActivity.FRAGMENT_SCRIPTS);

	private Sprite sprite;
	private Sprite copiedSprite;

	private UserDefinedBrick userDefinedBrickToDelete;
	private UserDefinedBrick differentUserDefinedBrick;
	private SetXBrick setXBrick;
	private IfLogicBeginBrick ifLogicBrick;

	private int indexStartScript = 2;
	private int indexBrickToDelete = 0;

	@After
	public void tearDown() throws IOException {
		TestUtils.deleteProjects(DeleteUserDefinedReceiverBrickTest.class.getSimpleName());
	}

	@Before
	public void setUp() throws IOException, CloneNotSupportedException {
		createProject(DeleteUserDefinedReceiverBrickTest.class.getSimpleName());
		baseActivityTestRule.launchActivity();
		onBrickAtPosition(0).performDeleteBrick();
	}

	@Test
	public void testRemoveFromYourBrickCategory() {
		assertFalse(sprite.getUserDefinedBrickList().contains(userDefinedBrickToDelete));
	}
	@Test
	public void testDeletionInCurrentSpriteOnly() {
		assertTrue(copiedSprite.containsUserDefinedBrickWithSameUserData(userDefinedBrickToDelete));
		UserDefinedBrick userDefinedBrick = (UserDefinedBrick) copiedSprite.getScript(indexStartScript).getBrickList().get(indexBrickToDelete);
		assertTrue(userDefinedBrick.isUserDefinedBrickDataEqual(userDefinedBrickToDelete));
	}
	@Test
	public void testDeletionInNestedBricks() {
		assertTrue(ifLogicBrick.getNestedBricks().isEmpty());
		assertTrue(ifLogicBrick.getSecondaryNestedBricks().isEmpty());
	}
	@Test
	public void testDeletionOfUserDefinedReceiverBrick() {
		assertFalse(sprite.getScript(indexStartScript).getBrickList().contains(userDefinedBrickToDelete));
	}
	@Test
	public void testDifferentUserDefinedBrickNotDeleted() {
		assertTrue(sprite.getScript(indexStartScript).getBrickList().contains(differentUserDefinedBrick));
	}

	private void createProject(String projectName) throws IOException, CloneNotSupportedException {
		Project project = UiTestUtils.createDefaultTestProject(projectName);
		ProjectManager projectManager = ProjectManager.getInstance();

		SpriteController controller = new SpriteController();

		sprite = new Sprite("Sprite1");
		userDefinedBrickToDelete = new UserDefinedBrick(Collections.singletonList(new UserDefinedBrickLabel("Label")));
		differentUserDefinedBrick = new UserDefinedBrick(Collections.singletonList(new UserDefinedBrickInput("Input")));
		setXBrick = new SetXBrick(new Formula(0));
		ifLogicBrick = new IfLogicBeginBrick();
		ifLogicBrick.addBrickToIfBranch(userDefinedBrickToDelete);
		ifLogicBrick.addBrickToElseBranch(userDefinedBrickToDelete);

		createUserDefinedScripts();
		createStartScript();

		copiedSprite = controller.copy(sprite, project, project.getDefaultScene());
		projectManager.setCurrentSprite(sprite);
	}

	private void createStartScript() {
		Script startScript = new StartScript();
		startScript.addBrick(userDefinedBrickToDelete);
		startScript.addBrick(differentUserDefinedBrick);
		startScript.addBrick(ifLogicBrick);
		sprite.addScript(startScript);
	}

	private void createUserDefinedScripts() throws CloneNotSupportedException {
		Script scriptToDelete = new UserDefinedScript();
		scriptToDelete.setScriptBrick(new UserDefinedReceiverBrick(userDefinedBrickToDelete));
		sprite.addUserDefinedBrick(userDefinedBrickToDelete);
		scriptToDelete.addBrick(setXBrick);

		Script secondScript = new UserDefinedScript();
		secondScript.setScriptBrick(new UserDefinedReceiverBrick(differentUserDefinedBrick));
		sprite.addUserDefinedBrick(differentUserDefinedBrick);
		secondScript.addBrick(setXBrick.clone());

		sprite.addScript(scriptToDelete);
		sprite.addScript(secondScript);
	}
}
