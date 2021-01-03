/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.ui.tabs;

import com.google.android.material.tabs.TabLayout;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.catrobat.catroid.R.id.tab_layout;
import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;
import static org.catrobat.catroid.ui.SpriteActivity.FRAGMENT_LOOKS;
import static org.catrobat.catroid.ui.SpriteActivity.FRAGMENT_SCRIPTS;
import static org.catrobat.catroid.ui.SpriteActivity.FRAGMENT_SOUNDS;
import static org.catrobat.catroid.uiespresso.util.actions.TabActionsKt.selectTabAtPosition;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

import static androidx.test.espresso.Espresso.onIdle;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(Parameterized.class)
public class TabLayoutActionModeTest {
	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, FRAGMENT_SCRIPTS);

	@Parameters
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{FRAGMENT_SCRIPTS},
				{FRAGMENT_LOOKS},
				{FRAGMENT_SOUNDS}
		});
	}

	@Parameter
	public Integer fragment;

	private Project project;

	@Before
	public void setUp() throws IOException {
		createProject();
		baseActivityTestRule.launchActivity();
	}

	@Test
	public void testFragment() {
		onView(withId(tab_layout)).perform(selectTabAtPosition(fragment));
		assertTabLayoutIsShown(fragment);
		openActionBarOverflowOrOptionsMenu(baseActivityTestRule.getActivity());
		onView(withText(R.string.delete)).perform(click());
		assertTabLayoutIsNotShown();
		pressBack();
		assertTabLayoutIsShown(fragment);
	}

	private void assertTabLayoutIsShown(int tabSelected) {
		onIdle();
		TabLayout tabLayout = baseActivityTestRule.getActivity().findViewById(tab_layout);
		assertNotNull(tabLayout);
		assertEquals(tabSelected, tabLayout.getSelectedTabPosition());
	}

	private void assertTabLayoutIsNotShown() {
		onIdle();
		assertNull(baseActivityTestRule.getActivity().findViewById(tab_layout));
	}

	private void createProject() throws IOException {
		project = new Project(ApplicationProvider.getApplicationContext(), "TabLayoutActionModeTest");
		Sprite sprite = new Sprite("testSprite");

		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentlyEditedScene(project.getDefaultScene());
		XstreamSerializer.getInstance().saveProject(project);

		Script script = new StartScript();
		sprite.addScript(script);
		sprite.getLookList().add(createLookData());
		sprite.getSoundList().add(createSoundInfo());
	}

	private LookData createLookData() {
		LookData lookData = new LookData();
		lookData.setFile(Mockito.mock(File.class));
		lookData.setName("look");
		return lookData;
	}

	private SoundInfo createSoundInfo() throws IOException {
		File soundFile = ResourceImporter.createSoundFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.raw.longsound,
				new File(project.getDefaultScene().getDirectory(), SOUND_DIRECTORY_NAME),
				"longsound.mp3");

		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setFile(soundFile);
		soundInfo.setName("sound");
		return soundInfo;
	}
}
