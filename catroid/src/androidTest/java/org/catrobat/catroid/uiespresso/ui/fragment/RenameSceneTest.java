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

package org.catrobat.catroid.uiespresso.ui.fragment;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
public class RenameSceneTest {

	private String projectName = "TestRenameScene";
	@Rule
	public BaseActivityInstrumentationRule<ProjectActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ProjectActivity.class);
	private Project project;

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testRenameScene() {
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.rename))
				.perform(click());

		onRecyclerView().atPosition(0)
				.performCheckItem();

		onView(withId(R.id.confirm))
				.perform(click());

		onView(withText(R.string.rename_scene_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		String oldSceneName = "Scene 1";
		String newSceneName = "firstScene";

		onView(allOf(withText(oldSceneName), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(newSceneName));

		closeSoftKeyboard();

		onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.perform(click());
		onView(withText(newSceneName))
				.check(matches(isDisplayed()));

		assertEquals(newSceneName, project.getDefaultScene().getName());
	}

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects(projectName);
	}

	@Before
	public void setUp() throws Exception {
		createProject(projectName);
		baseActivityTestRule.launchActivity();
	}

	private void createProject(String projectName) {
		project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		Scene secondScene = new Scene("secondScene", project);
		project.addScene(secondScene);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().saveProject(InstrumentationRegistry.getTargetContext());
	}
}
