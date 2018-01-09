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

package org.catrobat.catroid.uiespresso.content.brick.app;

import android.support.annotation.IdRes;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

@Category({Cat.AppUi.class, Level.Smoke.class})
@RunWith(Parameterized.class)
public class BroadcastBricksTest {
	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	private static int whenBrickPosition = 0;
	private static int broadcastSendPosition = 1;
	private static int broadcastReceivePosition = 2;
	private static int broadcastAndWaitPosition = 3;

	@Parameters(name = "{2}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{broadcastSendPosition, R.id.brick_broadcast_spinner, "BroadcastSendTest"},
				{broadcastReceivePosition, R.id.brick_broadcast_receive_spinner, "BroadcastReceiveTest"},
				{broadcastAndWaitPosition, R.id.brick_broadcast_wait_spinner, "BroadcastAndWaitTest"}
		});
	}

	@Parameter
	public int brickPosition;

	@Parameter(1)
	public @IdRes int spinnerId;

	@Parameter(2)
	public String testName;

	private String defaultMessage = "defaultMessage";

	@Before
	public void setUp() throws Exception {
		createProject("BroadcastBricksTest");
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void removeUnusedMessagesBroadcastTest() {
		onBrickAtPosition(whenBrickPosition).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(broadcastSendPosition).checkShowsText(R.string.brick_broadcast);
		onBrickAtPosition(broadcastReceivePosition).checkShowsText(R.string.brick_broadcast_receive);
		onBrickAtPosition(broadcastAndWaitPosition).checkShowsText(R.string.brick_broadcast_wait);

		onBrickAtPosition(brickPosition).onSpinner(spinnerId)
				.checkShowsText(defaultMessage);

		String newMessage = "newMessage";
		onBrickAtPosition(brickPosition).onSpinner(spinnerId)
				.perform(click());
		onView(withText(R.string.new_broadcast_message))
				.perform(click());
		onView(withId(R.id.edit_text))
				.perform(clearText(), typeTextIntoFocusedView(newMessage));
		onView(withText(R.string.ok))
				.perform(click());
		onBrickAtPosition(brickPosition).onSpinner(spinnerId)
				.checkShowsText(newMessage);

		onBrickAtPosition(brickPosition).onSpinner(spinnerId)
				.perform(click());
		onView(withText(defaultMessage))
				.perform(click());
		onBrickAtPosition(brickPosition).onSpinner(spinnerId)
				.checkShowsText(defaultMessage);

		onView(withId(R.id.button_play))
				.perform(click());

		pressBack();
		pressBack();

		onBrickAtPosition(brickPosition).onSpinner(spinnerId)
				.perform(click());
		onView(withText(newMessage))
				.check(doesNotExist());
	}

	private Script createProject(String projectName) {
		Project project = new Project(null, projectName);
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();

		sprite.addScript(script);

		script.addBrick(new BroadcastBrick(defaultMessage));
		script.addBrick(new BroadcastReceiverBrick(defaultMessage));
		script.addBrick(new BroadcastWaitBrick(defaultMessage));

		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		return script;
	}
}
