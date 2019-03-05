/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.io.asynctask.ProjectLoadTask;
import org.catrobat.catroid.io.asynctask.ProjectSaveTask;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.annotations.Flaky;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.TestCase.assertTrue;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BroadcastBrickDataInteractionWrapper.onBroadcastBrickAtPosition;

@RunWith(AndroidJUnit4.class)
public class BroadcastSendBrickMessageContainerTest {

	private String defaultMessage = "defaultMessage";
	private Project project;
	private Sprite sprite;

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

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

		onBroadcastBrickAtPosition(broadcastSendPosition)
				.onSpinner(R.id.brick_broadcast_spinner)
				.createNewBroadcastMessage(uselessMessage)
				.checkShowsText(uselessMessage);

		onBroadcastBrickAtPosition(broadcastSendPosition)
				.onSpinner(R.id.brick_broadcast_spinner)
				.perform(click());

		onView(withText(defaultMessage))
				.perform(click());

		onBroadcastBrickAtPosition(broadcastSendPosition)
				.onSpinner(R.id.brick_broadcast_spinner)
				.checkShowsText(defaultMessage);

		ProjectSaveTask
				.task(project);

		baseActivityTestRule.finishActivity();

		assertTrue(ProjectLoadTask
				.task(project.getDirectory(), InstrumentationRegistry.getTargetContext()));

		ProjectManager.getInstance().setCurrentSprite(sprite);

		baseActivityTestRule.launchActivity();

		onBroadcastBrickAtPosition(broadcastSendPosition)
				.onSpinner(R.id.brick_broadcast_spinner)
				.checkShowsText(defaultMessage);

		onBroadcastBrickAtPosition(broadcastSendPosition)
				.onSpinner(R.id.brick_broadcast_spinner)
				.perform(click());

		onView(withText(uselessMessage))
				.check(doesNotExist());
	}

	private void createProject(String projectName) {
		project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		sprite = new Sprite("testSprite");
		Script script = new StartScript();

		sprite.addScript(script);

		script.addBrick(new BroadcastBrick(defaultMessage));

		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
	}
}
