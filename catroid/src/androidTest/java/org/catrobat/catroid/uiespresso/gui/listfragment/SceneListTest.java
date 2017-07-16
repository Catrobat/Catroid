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

package org.catrobat.catroid.uiespresso.gui.listfragment;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.copypaste.ClipboardHandler;
import org.catrobat.catroid.data.ProjectInfo;
import org.catrobat.catroid.data.SceneInfo;
import org.catrobat.catroid.gui.activity.SceneListActivity;
import org.catrobat.catroid.projecthandler.ProjectHolder;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class SceneListTest {

	private ProjectInfo project;
	private SceneInfo sceneToRename;
	private SceneInfo sceneToDelete;

	@Rule
	public BaseActivityInstrumentationRule<SceneListActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SceneListActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testRename() {
		String newName = "New Name";
		onView(withId(R.id.fragment))
				.perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));
		onView(withId(R.id.btnEdit)).perform(click());

		onView(withText(R.string.rename_scene_dialog)).check(matches(isDisplayed()));

		onView(withId(R.id.edit_text)).perform(replaceText(newName));
		onView(withText(R.string.ok)).perform(click());

		assertEquals(newName, sceneToRename.getName());
	}

	@Test
	public void testDelete() {
		onView(withId(R.id.fragment))
				.perform(RecyclerViewActions.actionOnItemAtPosition(1, longClick()));
		onView(withId(R.id.btnDelete)).perform(click());

		assertFalse(project.getScenes().contains(sceneToDelete));
	}

	@Test
	public void testCopyAndPaste() throws Exception {
		onView(withId(R.id.fragment))
				.perform(RecyclerViewActions.actionOnItemAtPosition(2, longClick()));
		onView(withId(R.id.btnCopy)).perform(click());

		assertEquals(1, ClipboardHandler.getClipboard().getClipboardSize());
		onView(withId(R.id.btnPaste)).check(matches(isDisplayed()));

		int numberOfItems = project.getScenes().size();

		onView(withId(R.id.btnPaste)).perform(click());
		assertEquals(numberOfItems + 1, project.getScenes().size());
	}

	private void createProject() throws Exception {
		project = new ProjectInfo("Test");

		SceneInfo scene0 = new SceneInfo("Scene 0", project.getDirectoryInfo());
		SceneInfo scene1 = new SceneInfo("Scene 1", project.getDirectoryInfo());
		SceneInfo scene2 = new SceneInfo("Scene 2", project.getDirectoryInfo());
		SceneInfo scene3 = new SceneInfo("Scene 3", project.getDirectoryInfo());

		project.addScene(scene0);
		project.addScene(scene1);
		project.addScene(scene2);
		project.addScene(scene3);

		ProjectHolder.getInstance().setCurrentProject(project);

		sceneToRename = scene0;
		sceneToDelete = scene1;
	}
}
