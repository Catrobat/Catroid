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

package org.catrobat.catroid.uiespresso.content.brick;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.FileTestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.SpinnerUtils.checkIfSpinnerOnBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.util.FileTestUtils.saveFileToProject;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class SetLookBrickRenameDeleteLookTest {
	private static final int RESOURCE_LOOK = org.catrobat.catroid.test.R.raw.icon;
	private static final int RESOURCE_LOOK2 = org.catrobat.catroid.test.R.raw.icon2;

	private int brickPosition;
	private String lookName = "testLook1";
	private String lookName2 = "testLook2";

	@Rule
	public BaseActivityInstrumentationRule<ProgramMenuActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ProgramMenuActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testSpinnerUpdatesDelete() {
		openScriptView();
		checkIfSpinnerOnBrickAtPositionShowsString(R.id.brick_set_look_spinner, brickPosition, lookName);

		pressBack();
		onView(withId(R.id.program_menu_button_looks))
				.perform(click());
		deleteLook(lookName);
		openScriptView();
		checkIfSpinnerOnBrickAtPositionShowsString(R.id.brick_set_look_spinner, brickPosition,
				lookName2);

		BrickTestUtils.onScriptList().atPosition(brickPosition).onChildView(withId(R.id.brick_set_look_spinner))
				.perform(click());
		onView(withText(lookName))
				.check(doesNotExist());
		onView(withText(lookName2))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testSpinnerUpdatesRename() {
		String newName = "nameRenamed";

		openScriptView();
		checkIfSpinnerOnBrickAtPositionShowsString(R.id.brick_set_look_spinner, brickPosition, lookName);

		pressBack();
		onView(withId(R.id.program_menu_button_looks))
				.perform(click());
		renameLook(lookName, newName);
		openScriptView();
		checkIfSpinnerOnBrickAtPositionShowsString(R.id.brick_set_look_spinner, brickPosition, newName);

		BrickTestUtils.onScriptList().atPosition(brickPosition).onChildView(withId(R.id.brick_set_look_spinner))
				.perform(click());
		onView(withText(lookName))
				.check(doesNotExist());
		onView(withText(lookName2))
				.check(matches(isDisplayed()));
		onView(withText(newName))
				.check(matches(isDisplayed()));
	}

	private void deleteLook(String lookName) {
		openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
		onView(withText(R.string.delete))
				.perform(click());
		onView(withText(lookName))
				.perform(click());
		onView(withContentDescription(R.string.done))
				.perform(click());

		onView(allOf(withId(android.R.id.button1), withText(R.string.yes)))
				.check(matches(isDisplayed()));
		onView(allOf(withId(android.R.id.button2), withText(R.string.no)))
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button1), withText(R.string.yes)))
				.perform(click());

		pressBack();
	}

	private void renameLook(String oldName, String newName) {
		openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
		onView(withText(R.string.rename))
				.perform(click());
		onView(withText(oldName))
				.perform(click());
		onView(withContentDescription(R.string.done))
				.perform(click());

		onView(withText(R.string.rename_look_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));
		onView(allOf(withId(R.id.edit_text), withText(oldName), isDisplayed()))
				.perform(replaceText(newName));

		closeSoftKeyboard();

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.perform(click());

		onView(withText(newName))
				.check(matches(isDisplayed()));
		onView(withText(oldName))
				.check(doesNotExist());

		pressBack();
	}

	private void openScriptView() {
		onView(withId(R.id.program_menu_button_scripts))
				.perform(click());
	}

	private void createProject() {
		String projectName = "SetLookBrick";
		BrickTestUtils.createProjectAndGetStartScript(projectName).addBrick(new SetLookBrick());
		brickPosition = 1;

		ProjectManager projectManager = ProjectManager.getInstance();
		List<LookData> lookDataList = projectManager.getCurrentSprite().getLookDataList();
		Project project = projectManager.getCurrentProject();

		File lookFile = saveFileToProject(projectName, project.getDefaultScene().getName(), "image.png",
				RESOURCE_LOOK, getInstrumentation().getContext(), FileTestUtils.FileTypes.IMAGE);
		LookData lookData = new LookData();
		lookData.setLookFilename(lookFile.getName());
		lookData.setLookName(lookName);

		File lookFile2 = saveFileToProject(projectName, project.getDefaultScene().getName(), "image2.png",
				RESOURCE_LOOK2, getInstrumentation().getContext(), FileTestUtils.FileTypes.IMAGE);
		LookData lookData2 = new LookData();
		lookData2.setLookFilename(lookFile2.getName());
		lookData2.setLookName(lookName2);

		lookDataList.add(lookData);
		lookDataList.add(lookData2);
		projectManager.getFileChecksumContainer().addChecksum(lookData.getChecksum(), lookData.getAbsolutePath());
		projectManager.getFileChecksumContainer().addChecksum(lookData2.getChecksum(), lookData2.getAbsolutePath());
	}
}
