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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.DeleteLookBrick;
import org.catrobat.catroid.content.bricks.PaintNewLookBrick;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.IOException;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class DeleteLookBrickTest {
	private Sprite sprite;
	private StartScript script;
	private final String projectName = "DeleteLookBrickTest";

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		Project project = new Project(ApplicationProvider.getApplicationContext(),
				projectName);
		sprite = new Sprite("testSprite");
		script = new StartScript();
		script.addBrick(new PaintNewLookBrick());
		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentlyEditedScene(project.getDefaultScene());

		Intents.init();
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testPaintLookWithoutDelete() {
		onView(withId(R.id.button_play)).perform(click());
		onView(withId(R.id.pocketpaint_drawing_surface_view)).perform(click());
		pressBack();

		assertEquals(1, sprite.getLookList().size());
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testPaintAndDeleteLook() {
		script.addBrick(new DeleteLookBrick());

		onView(withId(R.id.button_play)).perform(click());
		onView(withId(R.id.pocketpaint_drawing_surface_view)).perform(click());
		pressBack();

		assertEquals(0, sprite.getLookList().size());
	}

	@After
	public void tearDown() throws IOException {
		Intents.release();
		baseActivityTestRule.finishActivity();
		TestUtils.deleteProjects(projectName);
	}
}
