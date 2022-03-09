/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.ui.activity.rtl;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.Locale;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;
import static org.koin.java.KoinJavaComponent.inject;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class HindiNumberAtShowDetailsAtProjectActivityTest {
	@Rule
	public FragmentActivityTestRule<ProjectActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(ProjectActivity.class, ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SPRITES);
	private Locale arLocale = new Locale("ar");

	@Before
	public void setUp() {
		SettingsFragment.setLanguageSharedPreference(getTargetContext(), "ar");
		createProject();
		baseActivityTestRule.launchActivity();

		openContextualActionModeOverflowMenu();
		onView(withText(R.string.show_details)).perform(click());
	}

	@After
	public void tearDown() {
		openContextualActionModeOverflowMenu();
		onView(withText(R.string.hide_details)).perform(click());
		SettingsFragment.removeLanguageSharedPreference(getTargetContext());
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.RTLTests.class})
	@Test
	public void hindiNumbers() throws Exception {
		assertEquals(arLocale.getDisplayLanguage(), Locale.getDefault().getDisplayLanguage());
		assertTrue(RtlUiTestUtils.checkTextDirectionIsRtl(Locale.getDefault().getDisplayName()));

		onRecyclerView().atPosition(1).onChildView(R.id.details_view)
				.check(matches(withText(String.format(Locale.getDefault(),
						UiTestUtils.getResourcesString(R.string.sprite_details),
						7, 2, 0
				))));
	}

	private void createProject() {
		String projectName = "HindiNumberTest";
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);

		Sprite firstSprite = new Sprite("firstSprite");

		Script firstScript = new StartScript();
		firstScript.addBrick(new SetXBrick(new Formula(BrickValues.X_POSITION)));
		firstScript.addBrick(new SetXBrick(new Formula(BrickValues.X_POSITION)));
		firstSprite.addScript(firstScript);

		Script secondScript = new StartScript();
		secondScript.addBrick(new SetXBrick(new Formula(BrickValues.X_POSITION)));
		secondScript.addBrick(new SetYBrick());
		secondScript.addBrick(new SetXBrick(new Formula(BrickValues.X_POSITION)));
		firstSprite.addScript(secondScript);

		LookData lookData = new LookData();
		lookData.setName("red");
		firstSprite.getLookList().add(lookData);

		LookData anotherLookData = new LookData();
		anotherLookData.setName("blue");
		firstSprite.getLookList().add(anotherLookData);

		project.getDefaultScene().addSprite(firstSprite);

		final ProjectManager projectManager = inject(ProjectManager.class).getValue();
		projectManager.setCurrentProject(project);
		projectManager.setCurrentlyEditedScene(project.getDefaultScene());
	}
}
