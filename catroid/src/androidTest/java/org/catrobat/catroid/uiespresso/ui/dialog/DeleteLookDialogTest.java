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

package org.catrobat.catroid.uiespresso.ui.dialog;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class DeleteLookDialogTest {

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	private String toBeDeletedLookName = "testLook2";

	@Before
	public void setUp() throws Exception {
		createProject("deleteLooksDialogTest");

		Intent intent = new Intent();
		intent.putExtra(ScriptActivity.EXTRA_FRAGMENT_POSITION, ScriptActivity.FRAGMENT_LOOKS);

		baseActivityTestRule.launchActivity(intent);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void deleteLookDialogTest() {
		openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
		onView(withText(R.string.delete)).perform(click());

		onView(withText(toBeDeletedLookName)).perform(click());
		onView(withContentDescription("Done")).perform(click());

		onView(withText(R.string.dialog_confirm_delete_look_title)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		onView(withText(R.string.dialog_confirm_delete_look_message)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button1), withText(R.string.yes)))
				.check(matches(isDisplayed()));
		onView(allOf(withId(android.R.id.button2), withText(R.string.no)))
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button1), withText(R.string.yes)))
				.perform(click());

		onView(withText(toBeDeletedLookName))
				.check(doesNotExist());
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void cancelDeleteLookDialogTest() {
		openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
		onView(withText(R.string.delete)).perform(click());

		onView(withText(toBeDeletedLookName)).perform(click());
		onView(withContentDescription("Done")).perform(click());

		onView(withText(R.string.dialog_confirm_delete_look_title)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		onView(withText(R.string.dialog_confirm_delete_look_message)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button1), withText(R.string.yes)))
				.check(matches(isDisplayed()));
		onView(allOf(withId(android.R.id.button2), withText(R.string.no)))
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button2), withText(R.string.no)))
				.perform(click());

		onView(withText(toBeDeletedLookName))
				.check(matches(isDisplayed()));
	}

	private void createProject(String projectName) {
		Project project = new Project(null, projectName);

		Sprite sprite = new SingleSprite("testSprite");
		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		File imageFile = UiTestUtils.saveFileToProject(
				projectName, ProjectManager.getInstance().getCurrentScene().getName(), "catroid_sunglasses.png",
				org.catrobat.catroid.test.R.drawable.catroid_banzai, InstrumentationRegistry.getTargetContext(),
				UiTestUtils.FileTypes.IMAGE
		);

		File imageFile2 = UiTestUtils.saveFileToProject(
				projectName, ProjectManager.getInstance().getCurrentScene().getName(), "catroid_banzai.png",
				org.catrobat.catroid.test.R.drawable.catroid_banzai, InstrumentationRegistry.getTargetContext(),
				UiTestUtils.FileTypes.IMAGE
		);

		List<LookData> lookDataList = ProjectManager.getInstance().getCurrentSprite().getLookDataList();
		LookData lookData = new LookData();
		lookData.setLookFilename(imageFile.getName());
		lookData.setLookName("testLook1");
		lookDataList.add(lookData);
		ProjectManager.getInstance().getFileChecksumContainer()
				.addChecksum(lookData.getChecksum(), lookData.getAbsolutePath());

		LookData lookData2 = new LookData();
		lookData2.setLookFilename(imageFile2.getName());
		lookData2.setLookName(toBeDeletedLookName);
		lookDataList.add(lookData2);
		ProjectManager.getInstance().getFileChecksumContainer()
				.addChecksum(lookData2.getChecksum(), lookData2.getAbsolutePath());
	}
}
