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

package org.catrobat.catroid.uiespresso.content.messagecontainer;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastMessageBrick;
import org.catrobat.catroid.rules.FlakyTestRule;
import org.catrobat.catroid.runner.Flaky;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.TestCase.assertTrue;

import static org.catrobat.catroid.io.asynctask.ProjectLoaderKt.loadProject;
import static org.catrobat.catroid.io.asynctask.ProjectSaverKt.saveProjectSerial;
import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.content.messagecontainer.BroadcastMessageBrickTestUtils.createNewBroadcastMessageOnBrick;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class BroadcastSendBrickMessageContainerTest {

	private final String defaultMessage = "defaultMessage";
	private Project project;
	private Sprite sprite;
	private BroadcastMessageBrick broadcastMessageBrick;

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Rule
	public FlakyTestRule flakyTestRule = new FlakyTestRule();

	@Before
	public void setUp() throws Exception {
		createProject(this.getClass().getSimpleName());
		baseActivityTestRule.launchActivity();
	}

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects(this.getClass().getSimpleName());
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	@Flaky
	public void testBroadcastSendBrickOmitSaveUnusedMessages() {
		String uselessMessage = "useless";
		int broadcastSendPosition = 1;

		createNewBroadcastMessageOnBrick(uselessMessage, broadcastMessageBrick,
				(SpriteActivity) baseActivityTestRule.getActivity());

		onBrickAtPosition(broadcastSendPosition)
				.onSpinner(R.id.brick_broadcast_spinner)
				.checkShowsText(uselessMessage);

		onBrickAtPosition(broadcastSendPosition)
				.onSpinner(R.id.brick_broadcast_spinner)
				.performSelectNameable(defaultMessage);

		onBrickAtPosition(broadcastSendPosition)
				.onSpinner(R.id.brick_broadcast_spinner)
				.checkShowsText(defaultMessage);

		saveProjectSerial(project, ApplicationProvider.getApplicationContext());

		baseActivityTestRule.finishActivity();

		assertTrue(loadProject(project.getDirectory(), ApplicationProvider.getApplicationContext()));

		ProjectManager.getInstance().setCurrentSprite(sprite);

		baseActivityTestRule.launchActivity();

		onBrickAtPosition(broadcastSendPosition)
				.onSpinner(R.id.brick_broadcast_spinner)
				.checkShowsText(defaultMessage);

		onBrickAtPosition(broadcastSendPosition)
				.onSpinner(R.id.brick_broadcast_spinner)
				.perform(click());

		onView(withText(uselessMessage))
				.check(doesNotExist());
	}

	private void createProject(String projectName) {
		project = UiTestUtils.createDefaultTestProject(projectName);
		sprite = UiTestUtils.getDefaultTestSprite(project);
		Script script = UiTestUtils.getDefaultTestScript(project);

		broadcastMessageBrick = new BroadcastBrick(defaultMessage);
		script.addBrick(broadcastMessageBrick);
	}
}
