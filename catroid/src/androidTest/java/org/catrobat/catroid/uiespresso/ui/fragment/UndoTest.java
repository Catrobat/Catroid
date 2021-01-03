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

package org.catrobat.catroid.uiespresso.ui.fragment;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static org.catrobat.catroid.WaitForConditionAction.waitFor;
import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.junit.Assert.assertEquals;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(Parameterized.class)
public class UndoTest {

	private final long waitThreshold = 5000;

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class,
			SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"SingleScript", 0},
				{"CompositeBrick", 1},
				{"SingleBrick", 2},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public int brickPosition;

	String initialProject;

	@After
	public void tearDown() throws IOException {
		TestUtils.deleteProjects(UndoTest.class.getSimpleName());
	}

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity();
	}

	@Test
	public void testUndoSpinnerActionVisible() {
		onBrickAtPosition(brickPosition)
				.performDeleteBrick();

		onView(withId(R.id.menu_undo))
				.perform(waitFor(isDisplayed(), waitThreshold));
	}

	@Test
	public void testUndo() {
		onBrickAtPosition(brickPosition).performDeleteBrick();

		onView(withId(R.id.menu_undo))
				.perform(click());

		onView(withId(R.id.menu_undo))
				.check(doesNotExist());

		String projectAfterUndo = getProjectAsXmlString();
		assertEquals(projectAfterUndo, initialProject);
	}

	public String getProjectAsXmlString() {
		return XstreamSerializer.getInstance().getXmlAsStringFromProject(ProjectManager.getInstance().getCurrentProject());
	}

	private void createProject() {
		Script script = BrickTestUtils.createProjectAndGetStartScript(UndoTest.class.getSimpleName());
		IfLogicBeginBrick compositeBrick = new IfLogicBeginBrick();
		compositeBrick.addBrickToIfBranch(new SetXBrick());
		compositeBrick.addBrickToElseBranch(new SetXBrick());
		script.addBrick(compositeBrick);

		XstreamSerializer.getInstance().saveProject(ProjectManager.getInstance().getCurrentProject());
		initialProject = getProjectAsXmlString();
	}
}
