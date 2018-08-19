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

package org.catrobat.catroid.uiespresso.content.brick.app;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.annotations.Flaky;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
public class BroadcastBricksTest {
	private String defaultMessage = "defaultMessage";

	private int broadcastSendPosition = 1;
	private int broadcastReceivePosition = 2;

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		createProjectAndGetStartScriptWithImages("BroadcastBrickTest");
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testBroadcastBricksLayout() {
		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(broadcastSendPosition).checkShowsText(R.string.brick_broadcast);
		onBrickAtPosition(broadcastReceivePosition).checkShowsText(R.string.brick_broadcast_receive);
	}

	@Category({Cat.AppUi.class, Level.Functional.class, Cat.Quarantine.class})
	@Test
	@Flaky
	public void testRemoveUnusedMessagesBroadcastSend() {
		String uselessMessage = "useless";

		createNewMessageOnSpinner(R.id.brick_broadcast_spinner, broadcastSendPosition, uselessMessage);

		onBrickAtPosition(broadcastSendPosition).onSpinner(R.id.brick_broadcast_spinner)
				.performSelectString(defaultMessage);

		onView(withId(R.id.button_play))
				.perform(click());
		pressBack();
		onView(withId(R.id.stage_dialog_button_back))
				.perform(click());
		onBrickAtPosition(broadcastSendPosition).onSpinner(R.id.brick_broadcast_spinner)
				.perform(click());

		onView(withText(uselessMessage)).check(doesNotExist());

		pressBack();

		onBrickAtPosition(broadcastSendPosition).onSpinner(R.id.brick_broadcast_spinner)
				.checkShowsText(defaultMessage);
	}

	@Category({Cat.AppUi.class, Level.Functional.class, Cat.Quarantine.class})
	@Test
	@Flaky
	public void testRemoveUnusedMessagesBroadcastReceive() {
		String uselessMessage = "useless";
		createNewMessageOnSpinner(R.id.brick_broadcast_spinner, broadcastReceivePosition, uselessMessage);

		onBrickAtPosition(broadcastReceivePosition).onSpinner(R.id.brick_broadcast_spinner)
				.performSelectString(defaultMessage);

		onView(withId(R.id.button_play))
				.perform(click());
		pressBack();
		onView(withId(R.id.stage_dialog_button_back))
				.perform(click());

		onBrickAtPosition(broadcastReceivePosition).onSpinner(R.id.brick_broadcast_spinner)
				.perform(click());

		onView(withText(uselessMessage)).check(doesNotExist());

		pressBack();

		onBrickAtPosition(broadcastReceivePosition).onSpinner(R.id.brick_broadcast_spinner)
				.checkShowsText(defaultMessage);
	}

	private Script createProjectAndGetStartScriptWithImages(String projectName) {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();

		sprite.addScript(script);

		script.addBrick(new BroadcastBrick(defaultMessage));
		script.addBrick(new BroadcastReceiverBrick(new BroadcastScript(defaultMessage)));

		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		return script;
	}

	public void createNewMessageOnSpinner(int spinnerResourceId, int position, String message) {
		onBrickAtPosition(position).onSpinner(spinnerResourceId)
				.perform(click());

		onView(withText(R.string.new_option))
				.perform(click());

		onView(allOf(withId(R.id.input_edit_text), isDisplayed(), instanceOf(EditText.class)))
				.perform(typeText(message));
		closeSoftKeyboard();

		onView(withId(android.R.id.button1))
				.perform(click());

		onBrickAtPosition(position).onSpinner(spinnerResourceId)
				.checkShowsText(message);
	}
}
