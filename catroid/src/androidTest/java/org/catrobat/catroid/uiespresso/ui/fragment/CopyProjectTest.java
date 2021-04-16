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

package org.catrobat.catroid.uiespresso.ui.fragment;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.io.asynctask.ProjectSaveTask;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.ProjectListActivity;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.Z_INDEX_BACKGROUND;
import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class CopyProjectTest {

	@Rule
	public BaseActivityTestRule<ProjectListActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(ProjectListActivity.class, true, false);

	private String toBeCopiedProjectName = "testProject";

	@Before
	public void setUp() throws Exception {
		createProject(toBeCopiedProjectName);
		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void copyProjectTest() {
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.copy)).perform(click());

		onRecyclerView().atPosition(0)
				.performCheckItem();

		onView(withId(R.id.confirm))
				.perform(click());

		onView(withText(toBeCopiedProjectName))
				.check(matches(isDisplayed()));

		onView(withText(toBeCopiedProjectName + " (1)"))
				.check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void copyCopiedProjectsTest() {
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.copy)).perform(click());

		onRecyclerView().atPosition(0)
				.performCheckItem();

		onView(withId(R.id.confirm))
				.perform(click());

		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.copy)).perform(click());

		onRecyclerView().atPosition(0)
				.performCheckItem();

		onRecyclerView().atPosition(1)
				.performCheckItem();

		onView(withId(R.id.confirm))
				.perform(click());

		onView(withText(toBeCopiedProjectName))
				.check(matches(isDisplayed()));

		onView(withText(toBeCopiedProjectName + " (1)"))
				.check(matches(isDisplayed()));

		onView(withText(toBeCopiedProjectName + " (2)"))
				.check(matches(isDisplayed()));

		onView(withText(toBeCopiedProjectName + " (3)"))
				.check(matches(isDisplayed()));
	}

	private void createProject(String projectName) throws IOException {
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		Sprite sprite = new Sprite("firstSprite");
		Script script = new StartScript();
		script.addBrick(new SetXBrick(new Formula(BrickValues.X_POSITION)));
		sprite.addScript(script);

		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		XstreamSerializer.getInstance().saveProject(project);

		File soundFile = ResourceImporter.createSoundFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.raw.longsound,
				new File(project.getDefaultScene().getDirectory(), SOUND_DIRECTORY_NAME),
				"longsound.mp3");

		List<SoundInfo> soundInfoList = sprite.getSoundList();
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setFile(soundFile);
		soundInfo.setName("testSound1");
		soundInfoList.add(soundInfo);

		File imageFile = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.drawable.catroid_banzai,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				"catroid_sunglasses.png",
				1);

		List<LookData> lookDataList = sprite.getLookList();
		LookData lookData = new LookData();
		lookData.setFile(imageFile);
		lookData.setName("testLook1");
		lookDataList.add(lookData);

		Scene secondScene = new Scene("secondScene", project);

		Sprite backgroundSprite = new Sprite(ApplicationProvider.getApplicationContext().getString(R.string.background));
		backgroundSprite.look.setZIndex(Z_INDEX_BACKGROUND);
		secondScene.addSprite(backgroundSprite);

		project.addScene(secondScene);

		ProjectSaveTask
				.task(project, ApplicationProvider.getApplicationContext());
	}
}
