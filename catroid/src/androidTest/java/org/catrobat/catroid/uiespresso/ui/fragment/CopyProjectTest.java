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

package org.catrobat.catroid.uiespresso.ui.fragment;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.ProjectListActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewActions;
import org.catrobat.catroid.uiespresso.util.FileTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;

@RunWith(AndroidJUnit4.class)
public class CopyProjectTest {

	@Rule
	public BaseActivityInstrumentationRule<ProjectListActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ProjectListActivity.class, true, false);

	private String toBeCopiedProjectName = "testProject";

	@Before
	public void setUp() throws Exception {
		createProject(toBeCopiedProjectName);

		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void copyProjectTest() throws Exception {
		RecyclerViewActions.openOverflowMenu();
		onView(withText(R.string.copy)).perform(click());

		onRecyclerView().atPosition(0)
			.performCheckItem();

		onView(withText(R.string.confirm)).perform(click());

		onView(withText(toBeCopiedProjectName))
				.check(matches(isDisplayed()));

		onView(withText(toBeCopiedProjectName + " (1)"))
				.check(matches(isDisplayed()));

		ProjectManager.getInstance().loadProject(toBeCopiedProjectName + " (1)",
				InstrumentationRegistry.getTargetContext());
	}

	private void createProject(String projectName) {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		Sprite sprite = new SingleSprite("firstSprite");
		Script script = new StartScript();
		script.addBrick(new SetXBrick());
		sprite.addScript(script);

		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		File soundFile = FileTestUtils.saveFileToProject(
				projectName, ProjectManager.getInstance().getCurrentScene().getName(), "longsound.mp3",
				org.catrobat.catroid.test.R.raw.longsound, InstrumentationRegistry.getContext(),
				FileTestUtils.FileTypes.SOUND
		);
		List<SoundInfo> soundInfoList = sprite.getSoundList();
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setFileName(soundFile.getName());
		soundInfo.setName("testSound1");
		soundInfoList.add(soundInfo);

		File imageFile = FileTestUtils.saveFileToProject(
				projectName, ProjectManager.getInstance().getCurrentScene().getName(), "catroid_sunglasses.png",
				org.catrobat.catroid.test.R.drawable.catroid_banzai, InstrumentationRegistry.getContext(),
				FileTestUtils.FileTypes.IMAGE
		);
		List<LookData> lookDataList = sprite.getLookList();
		LookData lookData = new LookData();
		lookData.setFileName(imageFile.getName());
		lookData.setName("testLook1");
		lookDataList.add(lookData);

		Scene secondScene = new Scene(InstrumentationRegistry.getTargetContext(), "secondScene", project);
		project.addScene(secondScene);

		ProjectManager.getInstance().setCurrentScene(project.getDefaultScene());
		StorageHandler.getInstance().saveProject(project);
	}
}
