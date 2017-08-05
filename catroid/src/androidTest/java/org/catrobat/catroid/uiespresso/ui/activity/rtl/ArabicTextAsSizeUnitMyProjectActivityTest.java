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

package org.catrobat.catroid.uiespresso.ui.activity.rtl;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.MyProjectsActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.FileTestUtils;
import org.catrobat.catroid.utils.UtilUi;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.Locale;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.common.Constants.LANGUAGE_TAG_KEY;
import static org.catrobat.catroid.uiespresso.ui.activity.rtl.RtlUiTestUtils.checkTextDirection;
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.getResourcesString;
import static org.hamcrest.Matchers.containsString;

@RunWith(AndroidJUnit4.class)
public class ArabicTextAsSizeUnitMyProjectActivityTest {
	@Rule
	public BaseActivityInstrumentationRule<MyProjectsActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(MyProjectsActivity.class);
	private String expectedSize = "٥٫٤";
	private String arabicKb = "كيلوبايت";
	private Locale arLocale = new Locale("ar");
	private Locale defaultLocale = Locale.getDefault();

	@Before
	public void setUp() throws Exception {
		ProjectManager.getInstance().deleteCurrentProject(getTargetContext());
		createProjectWithBlueSprite("newTest");
		SettingsActivity.updateLocale(getTargetContext(), "ar", "");
		baseActivityTestRule.launchActivity(null);
	}

	@After
	public void tearDown() throws Exception {
		resetToDefaultLanguage();
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void name() throws Exception {
		assertEquals(Locale.getDefault().getDisplayLanguage(), arLocale.getDisplayLanguage());
		assertTrue(checkTextDirection(Locale.getDefault().getDisplayName()));

		openActionBarOverflowOrOptionsMenu(baseActivityTestRule.getActivity());

		onView(withText(R.string.show_details))
				.perform(click());

		onView(withId(R.id.details_right_bottom))
				.check(matches(withText(containsString(arabicKb))));
		onView(withId(R.id.details_right_bottom))
				.check(matches(withText(expectedSize + " " + getResourcesString(R.string.KiloByte_short))
				));
	}

	public Project createProjectWithBlueSprite(String projectName) {
		Project project = new Project(null, projectName);

		// blue Sprite
		Sprite blueSprite = new SingleSprite("blueSprite");
		StartScript blueStartScript = new StartScript();
		LookData blueLookData = new LookData();
		String blueImageName = "blue_image.bmp";

		blueLookData.setLookName(blueImageName);

		blueSprite.getLookDataList().add(blueLookData);

		blueStartScript.addBrick(new PlaceAtBrick(0, 0));
		blueStartScript.addBrick(new SetSizeToBrick(5000));
		blueSprite.addScript(blueStartScript);

		project.getDefaultScene().addSprite(blueSprite);

		StorageHandler.getInstance().saveProject(project);
		File blueImageFile = FileTestUtils.saveFileToProject(project.getName(), project.getDefaultScene().getName(),
				blueImageName,
				org.catrobat.catroid.test.R.raw.blue_image, InstrumentationRegistry.getContext(),
				FileTestUtils.FileTypes.IMAGE);

		blueLookData.setLookFilename(blueImageFile.getName());

		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(blueSprite);
		UtilUi.updateScreenWidthAndHeight(InstrumentationRegistry.getContext());

		return project;
	}

	private void resetToDefaultLanguage() {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext())
				.edit();
		editor.putString(LANGUAGE_TAG_KEY, defaultLocale.getLanguage());
		editor.commit();
		SettingsActivity.updateLocale(InstrumentationRegistry.getTargetContext(), defaultLocale.getLanguage(),
				defaultLocale.getCountry());
	}
}
