/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.File;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.TestCase.assertEquals;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;
import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;

@RunWith(AndroidJUnit4.class)
public class CopySceneTest {
	@Rule
	public BaseActivityInstrumentationRule<ProjectActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ProjectActivity.class);
	private String projectName = "CopySceneTest";
	private String toBeCopiedSceneName = "Scene 1";

	@Before
	public void setUp() throws Exception {
		createProject(InstrumentationRegistry.getTargetContext(), projectName);
		baseActivityTestRule.launchActivity();
	}

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects(projectName);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void name() {
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.copy))
				.perform(click());

		onRecyclerView().atPosition(0)
				.performCheckItem();

		onView(withId(R.id.confirm))
				.perform(click());

		onView(withText(toBeCopiedSceneName))
				.check(matches(isDisplayed()));

		onView(withText(toBeCopiedSceneName + " (1)"))
				.check(matches(isDisplayed()));
		int copiedSceneSpritesCount =
				ProjectManager.getInstance().getCurrentProject().getSceneList().get(2).getSpriteList().size();
		int copiedSceneLooksCount =
				ProjectManager.getInstance().getCurrentProject().getSceneList().get(2).getSpriteList().get(1).getLookList().size();
		int copiedSceneSoundFilesCount =
				ProjectManager.getInstance().getCurrentProject().getSceneList().get(2).getSpriteList().get(1).getSoundList().size();

		assertEquals(2, copiedSceneSpritesCount);
		assertEquals(1, copiedSceneLooksCount);
		assertEquals(1, copiedSceneSoundFilesCount);
	}

	private void createProject(Context context, String projectName) throws Exception {

		Project project = new Project(context, projectName);
		Sprite sprite = new SingleSprite("firstSprite");
		project.getDefaultScene().addSprite(sprite);
		Scene scene2 = new Scene("Scene 2", project);
		project.addScene(scene2);

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		XstreamSerializer.getInstance().saveProject(project);

		Script script = new StartScript();
		script.addBrick(new SetXBrick(new Formula(BrickValues.X_POSITION)));
		sprite.addScript(script);

		File imageFile = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getContext().getResources(),
				org.catrobat.catroid.test.R.drawable.catroid_banzai,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				"catroid_sunglasses.png",
				1);

		LookData lookData = new LookData("sunGlasses", imageFile);
		sprite.getLookList().add(lookData);

		File soundFile = ResourceImporter.createSoundFileFromResourcesInDirectory(
				InstrumentationRegistry.getContext().getResources(),
				org.catrobat.catroid.test.R.raw.longsound,
				new File(project.getDefaultScene().getDirectory(), SOUND_DIRECTORY_NAME),
				"longsound.mp3");

		SoundInfo soundInfo = new SoundInfo("sound", soundFile);
		sprite.getSoundList().add(soundInfo);
	}
}
