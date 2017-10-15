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

package org.catrobat.catroid.phiro.uiespresso.ui.activity;

import android.support.test.espresso.DataInteraction;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.NoteBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.MyProjectsActivity;
import org.catrobat.catroid.ui.fragment.ProjectListFragment;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.matchers.ProjectListMatchers;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.uiespresso.util.matchers.ProjectListMatchers.withProjectName;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
public class PhiroMyProjectsActivityTest {

	@Rule
	public BaseActivityInstrumentationRule<MyProjectsActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(MyProjectsActivity.class, true, false);

	@Before
	public void setUp() {
		deletePhiroPrograms();
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void phiroProjectsInitiallyLoadedTest() {
		for (String projectName : ProjectListFragment.PHIRO_PROJECTS) {
			onProjectWithName(projectName)
					.check(matches(isDisplayed()));
		}
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void phiroProgramRestoredWhenDeletedTest() {
		String projectName = ProjectListFragment.PHIRO_PROJECTS[0];
		StorageHandler.getInstance().deleteProject(projectName);

		relaunchActivity();

		onProjectWithName(projectName)
				.check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void phiroProgramsUnchangedWhenModifiedTest() {
		addNoteBrickToFirstScript(ProjectListFragment.PHIRO_PROJECTS[0]);

		relaunchActivity();

		checkAddedNoteBrickIsStillInFirstScript();
	}

	private void relaunchActivity() {
		baseActivityTestRule.getActivity().finish();
		baseActivityTestRule.launchActivity(null);
	}

	private void addNoteBrickToFirstScript(String projectName) {
		Project project = StorageHandler.getInstance().loadProject(projectName, null);
		ProjectManager.getInstance().setProject(project);
		project.getDefaultScene().getSpriteList().get(0).getScript(0).addBrick(new NoteBrick("I was added!"));
	}

	private void checkAddedNoteBrickIsStillInFirstScript() {
		Project project = ProjectManager.getInstance().getCurrentProject();
		Script script = project.getDefaultScene().getSpriteList().get(0).getScript(0);
		Brick brick = script.getBrick(script.getBrickList().size() - 1);
		assertTrue("Added NoteBrick is not available in first script anymore, but should be!", brick instanceof NoteBrick);
	}

	private void deletePhiroPrograms() {
		for (String project : ProjectListFragment.PHIRO_PROJECTS) {
			if (StorageHandler.getInstance().projectExists(project)) {
				StorageHandler.getInstance().deleteProject(project);
			}
		}
	}

	private DataInteraction onProjectWithName(String projectName) {
		return onData(allOf(instanceOf(ProjectData.class), withProjectName(projectName)))
				.inAdapterView(ProjectListMatchers.isProjectListView());
	}
}
