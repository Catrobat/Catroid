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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BroadcastBrickDataInteractionWrapper.onBroadcastBrickAtPosition;

@RunWith(AndroidJUnit4.class)
public class BroadcastBricksTest {
	private String defaultMessage = "defaultMessage";
	String message = "Oida!";

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

	@Test
	public void testAllBroadcastBricksSpinnersShowTheNewAddedMessage() {
		int firstBrickPosition = 0;

		onBroadcastBrickAtPosition(firstBrickPosition)
				.onSpinner(R.id.brick_broadcast_spinner)
				.createNewBroadcastMessage(message)
				.checkShowsText(message);

		List<String> spinnerValues = new ArrayList<>();
		spinnerValues.add(InstrumentationRegistry.getTargetContext().getString(R.string.new_option));
		spinnerValues.add(defaultMessage);
		spinnerValues.add(message);

		onBroadcastBrickAtPosition(1)
				.onSpinner(R.id.brick_broadcast_spinner)
				.checkNameableValuesAvailable(spinnerValues);
		onBroadcastBrickAtPosition(2)
				.onSpinner(R.id.brick_broadcast_spinner)
				.checkNameableValuesAvailable(spinnerValues);
		onBroadcastBrickAtPosition(3)
				.onSpinner(R.id.brick_broadcast_spinner)
				.checkNameableValuesAvailable(spinnerValues);
		onBroadcastBrickAtPosition(4)
				.onSpinner(R.id.brick_broadcast_spinner)
				.checkNameableValuesAvailable(spinnerValues);
		onBroadcastBrickAtPosition(5)
				.onSpinner(R.id.brick_broadcast_spinner)
				.checkNameableValuesAvailable(spinnerValues);
	}

	private void createProject(String projectName) {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		Sprite sprite = new Sprite("testSprite");

		Script script1 = new BroadcastScript(defaultMessage);
		Script script2 = new BroadcastScript(defaultMessage);

		script1.addBrick(new BroadcastBrick(defaultMessage));
		script1.addBrick(new BroadcastWaitBrick(defaultMessage));
		script2.addBrick(new BroadcastBrick(defaultMessage));
		script2.addBrick(new BroadcastWaitBrick(defaultMessage));

		sprite.addScript(script1);
		sprite.addScript(script2);

		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
	}
}
