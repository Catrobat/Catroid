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

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.FileTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;
import java.util.Locale;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
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
public class ArabicStringAsSizeUnitSoundsTest {
	private static final int RESOURCE_SOUND = org.catrobat.catroid.test.R.raw.longsound;
	private Locale arLocale = new Locale("ar");
	private Locale defaultLocale = Locale.getDefault();
	private String arabicKb = "كيلوبايت";
	private String expectedSize = "٣٫٧";
	private String soundName = "testSound1";
	private File soundFile;
	private List<SoundInfo> soundInfoList;
	private Intent intent = new Intent();

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> activityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject();
		SettingsActivity.updateLocale(getTargetContext(), "ar", "");
		intent.putExtra(ScriptActivity.EXTRA_FRAGMENT_POSITION, ScriptActivity.FRAGMENT_SOUNDS);
		activityTestRule.launchActivity(intent);
		getInstrumentation().runOnMainSync(new Runnable() {
			public void run() {
				activityTestRule.getActivity().getFragment(ScriptActivity.FRAGMENT_SOUNDS).setShowDetails(true);
			}
		});
	}

	@After
	public void tearDown() throws Exception {
		getInstrumentation().runOnMainSync(new Runnable() {
			public void run() {
				activityTestRule.getActivity().getFragment(ScriptActivity.FRAGMENT_SOUNDS).setShowDetails(false);
			}
		});
		resetToDefaultLanguage();
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void name() throws Exception {
		assertEquals(Locale.getDefault().getDisplayLanguage(), arLocale.getDisplayLanguage());
		assertTrue(checkTextDirection(Locale.getDefault().getDisplayName()));

		onView(withId(R.id.fragment_sound_item_size_text_view))
				.check(matches(withText(containsString(arabicKb))));
		onView(withId(R.id.fragment_sound_item_size_text_view))
				.check(matches(withText(expectedSize + " " + getResourcesString(R.string
						.KiloByte_short))));
	}

	private void createProject() {
		String projectName = "SoundFileSizeTest";
		SoundManager.getInstance();
		Script startScript = BrickTestUtils.createProjectAndGetStartScript(projectName);

		startScript.addBrick(new PlaySoundAndWaitBrick());
		startScript.addBrick(new PlaySoundBrick());

		soundFile = FileTestUtils.saveFileToProject(projectName, ProjectManager.getInstance().getCurrentScene()
						.getName(),
				"longsound.mp3", RESOURCE_SOUND, InstrumentationRegistry.getTargetContext(),
				FileTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle(soundName);

		soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		soundInfoList.add(soundInfo);

		ProjectManager.getInstance().getFileChecksumContainer()
				.addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());
	}

	private void resetToDefaultLanguage() {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(getTargetContext())
				.edit();
		editor.putString(LANGUAGE_TAG_KEY, defaultLocale.getLanguage());
		editor.commit();
		SettingsActivity.updateLocale(getTargetContext(), defaultLocale.getLanguage(),
				defaultLocale.getCountry());
	}
}
