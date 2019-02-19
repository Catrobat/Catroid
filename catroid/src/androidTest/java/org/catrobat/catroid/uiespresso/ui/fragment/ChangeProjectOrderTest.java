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

import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.ui.ProjectListActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.catrobat.catroid.utils.FileMetaDataExtractor;
import org.catrobat.catroid.utils.PathBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;

import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
@RunWith(AndroidJUnit4.class)
public class ChangeProjectOrderTest {

	@Rule
	public BaseActivityInstrumentationRule<ProjectListActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ProjectListActivity.class, true, false);

	private String projectOneName = "ProjectOneName";
	private String projectTwoName = "ProjectTwoName";

	@Before
	public void setUp() throws Exception {
		createProject(projectOneName);
		createProject(projectTwoName);

		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void changeProjectOrderTest() {
		File codeFileProjectOne = new File(PathBuilder.buildPath(PathBuilder.buildProjectPath(projectOneName),
				Constants.CODE_XML_FILE_NAME));
		assertTrue(codeFileProjectOne.setLastModified(codeFileProjectOne.lastModified() - 2000));

		File codeFileProjectTwo = new File(PathBuilder.buildPath(PathBuilder.buildProjectPath(projectTwoName),
				Constants.CODE_XML_FILE_NAME));
		assertTrue(codeFileProjectTwo.setLastModified(codeFileProjectTwo.lastModified() - 2000));

		onRecyclerView().performOnItemWithText(projectTwoName, click());
		pressBack();

		List<ProjectData> items = new ArrayList<>();
		for (String projectName : FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY)) {
			File codeFile = new File(PathBuilder.buildPath(PathBuilder.buildProjectPath(projectName), Constants.CODE_XML_FILE_NAME));
			items.add(new ProjectData(projectName, codeFile.lastModified()));
		}

		Collections.sort(items, new Comparator<ProjectData>() {
			@Override
			public int compare(ProjectData project1, ProjectData project2) {
				return Long.compare(project2.lastUsed, project1.lastUsed);
			}
		});

		assertEquals(items.get(0).projectName, projectTwoName);

		assertTrue(codeFileProjectOne.setLastModified(codeFileProjectOne.lastModified() - 2000));
		assertTrue(codeFileProjectTwo.setLastModified(codeFileProjectTwo.lastModified() - 2000));

		onRecyclerView().performOnItemWithText(projectOneName, click());

		pressBack();

		items.clear();
		for (String projectName : FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY)) {
			File codeFile = new File(PathBuilder.buildPath(PathBuilder.buildProjectPath(projectName), Constants.CODE_XML_FILE_NAME));
			items.add(new ProjectData(projectName, codeFile.lastModified()));
		}

		Collections.sort(items, new Comparator<ProjectData>() {
			@Override
			public int compare(ProjectData project1, ProjectData project2) {
				return Long.compare(project2.lastUsed, project1.lastUsed);
			}
		});

		assertEquals(items.get(0).projectName, projectOneName);
	}

	private void createProject(String projectName) {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		Sprite sprite = new SingleSprite("firstSprite");
		Script script = new StartScript();
		script.addBrick(new SetXBrick(new Formula(BrickValues.X_POSITION)));
		script.addBrick(new SetXBrick(new Formula(BrickValues.X_POSITION)));
		sprite.addScript(script);

		project.getDefaultScene().addSprite(sprite);

		XstreamSerializer.getInstance().saveProject(project);
	}
}
