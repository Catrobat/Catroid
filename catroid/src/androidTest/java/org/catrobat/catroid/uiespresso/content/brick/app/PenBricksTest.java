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
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.PenDownBrick;
import org.catrobat.catroid.content.bricks.PenUpBrick;
import org.catrobat.catroid.content.bricks.SetPenSizeBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;

@RunWith(AndroidJUnit4.class)
public class PenBricksTest {
	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testPenDownBrick() {
		int positionStartedBrick = 0;
		int positionPenDownBrick = 1;

		onBrickAtPosition(positionStartedBrick)
				.checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(positionPenDownBrick)
				.checkShowsText(R.string.brick_pen_down);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testPenUpBrick() {
		int positionPenUpBrick = 2;

		onBrickAtPosition(positionPenUpBrick)
				.checkShowsText(R.string.brick_pen_up);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testPenSizeBrick() {
		int positionPenSizeBrick = 3;

		onBrickAtPosition(positionPenSizeBrick)
				.checkShowsText(R.string.brick_pen_size);
		onBrickAtPosition(positionPenSizeBrick).onFormulaTextField(R.id.brick_set_pen_size_edit_text)
				.perform(click());
		onFormulaEditor()
				.performEnterNumber(100);
		onFormulaEditor()
				.performCloseAndSave();
		onBrickAtPosition(positionPenSizeBrick).onFormulaTextField(R.id.brick_set_pen_size_edit_text)
				.checkShowsNumber(100);
	}

	private void createProject() {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), "penBricksTest");
		Script startScript = BrickTestUtils.createProjectAndGetStartScript("penBricksTest");
		Sprite sprite1 = new Sprite("testSprite");
		sprite1.addScript(startScript);
		startScript.addBrick(new PenDownBrick());
		startScript.addBrick(new PenUpBrick());
		startScript.addBrick(new SetPenSizeBrick(5));
		project.getDefaultScene().addSprite(sprite1);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite1);
	}
}
