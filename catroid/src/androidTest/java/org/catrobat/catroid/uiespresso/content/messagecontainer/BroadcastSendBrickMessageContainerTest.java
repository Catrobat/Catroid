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
import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
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
public class BroadcastSendBrickMessageContainerTest {
	private String defaultMessage = "defaultMessage";
	private Project project;
	private Sprite sprite;
	private int broadcastSendPosition = 1;

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		createProject("BroadcastBrickTest");
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	@Flaky
	public void testBroadcastSendBrickOmitSaveUnusedMessages() throws Exception {
		String uselessMessage = "useless";
		createNewMessageOnSpinner(R.id.brick_broadcast_spinner, broadcastSendPosition, uselessMessage);
		onBrickAtPosition(broadcastSendPosition).onSpinner(R.id.brick_broadcast_spinner)
				.checkShowsText(uselessMessage);
		onBrickAtPosition(broadcastSendPosition).onSpinner(R.id.brick_broadcast_spinner)
				.perform(click());
		onView(withText(defaultMessage))
				.perform(click());
		onBrickAtPosition(broadcastSendPosition).onSpinner(R.id.brick_broadcast_spinner)
				.checkShowsText(defaultMessage);

		ProjectManager.getInstance().saveProject(InstrumentationRegistry.getTargetContext());

		baseActivityTestRule.finishActivity();

		ProjectManager.getInstance().loadProject(project.getName(), InstrumentationRegistry.getTargetContext());
		ProjectManager.getInstance().setCurrentSprite(sprite);

		baseActivityTestRule.launchActivity();

		onBrickAtPosition(broadcastSendPosition).onSpinner(R.id.brick_broadcast_spinner)
				.checkShowsText(defaultMessage);
		onBrickAtPosition(broadcastSendPosition).onSpinner(R.id.brick_broadcast_spinner)
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

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
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
