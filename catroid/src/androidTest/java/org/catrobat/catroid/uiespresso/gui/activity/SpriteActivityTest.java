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

package org.catrobat.catroid.uiespresso.gui.activity;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;

import org.catrobat.catroid.R;
import org.catrobat.catroid.data.LookInfo;
import org.catrobat.catroid.data.ProjectInfo;
import org.catrobat.catroid.data.SceneInfo;
import org.catrobat.catroid.data.SoundInfo;
import org.catrobat.catroid.data.SpriteInfo;
import org.catrobat.catroid.gui.activity.SpriteActivity;
import org.catrobat.catroid.gui.fragment.SceneListFragment;
import org.catrobat.catroid.gui.fragment.SpriteListFragment;
import org.catrobat.catroid.projecthandler.ProjectHolder;
import org.catrobat.catroid.storage.StorageManager;
import org.catrobat.catroid.test.StorageUtil;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertFalse;

public class SpriteActivityTest {

	private ProjectInfo project;
	private SpriteInfo sprite0;

	private LookInfo lookToDelete;
	private SoundInfo soundToDelete;

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject();
		Intent intent = new Intent();
		intent.putExtra(SceneListFragment.SELECTED_SCENE, "Scene 0");
		intent.putExtra(SpriteListFragment.SELECTED_SPRITE, "Sprite 0");
		baseActivityTestRule.launchActivity(intent);
	}

	@Test
	public void testViewPagerTitles() {
		onView(withId(R.id.pager)).check(matches(isDisplayed()));
		onView(withText(R.string.looks)).check(matches(isDisplayed()));
		onView(withText(R.string.sounds)).check(matches(isDisplayed()));
		onView(withText(R.string.scripts)).check(matches(isDisplayed()));
	}

	@Test
	public void testIfAllItemsAreDisplayed() {
		onView(withText(R.string.looks)).perform(click());

		for (LookInfo look : sprite0.getLooks()) {
			onView(withText(look.getName())).check(matches(isDisplayed()));
		}

		onView(withText(R.string.sounds)).perform(click());

		for (SoundInfo sound : sprite0.getSounds()) {
			onView(withText(sound.getName())).check(matches(isDisplayed()));
		}
	}

	@Test
	public void testDelete() throws Exception {
		List<String> deletedFilenames = new ArrayList<>();
		onView(withText(R.string.looks)).perform(click());

		onView(allOf(withId(R.id.recycler_view), isDisplayed()))
				.perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));
		onView(withId(R.id.btnDelete)).perform(click());

		assertFalse(sprite0.getLooks().contains(lookToDelete));
		deletedFilenames.add(lookToDelete.getFilePathInfo().getRelativePath());

		onView(withText(R.string.sounds)).perform(click());

		onView(allOf(withId(R.id.recycler_view), isDisplayed()))
				.perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));
		onView(withId(R.id.btnDelete)).perform(click());

		assertFalse(sprite0.getSounds().contains(soundToDelete));
		deletedFilenames.add(soundToDelete.getFilePathInfo().getRelativePath());

		File projectFolder = new File(project.getDirectoryInfo().getAbsolutePath());
		assertFalse(StorageUtil.directoryContains(projectFolder, deletedFilenames));
	}

	private void createProject() throws Exception {
		project = new ProjectInfo("Test");

		SceneInfo scene0 = new SceneInfo("Scene 0", project.getDirectoryInfo());
		sprite0 = new SpriteInfo("Sprite 0", project.getDirectoryInfo());

		lookToDelete = new LookInfo("Look 0", StorageManager.saveDrawableToSDCard(R.drawable
				.default_project_bird_wing_up,
				project.getDirectoryInfo(),
				InstrumentationRegistry.getTargetContext()));

		sprite0.addLook(lookToDelete);

		sprite0.addLook(new LookInfo("Look 1", StorageManager.saveDrawableToSDCard(R.drawable
				.default_project_bird_wing_up,
				project.getDirectoryInfo(),
				InstrumentationRegistry.getTargetContext()
		)));

		sprite0.addLook(new LookInfo("Look 2", StorageManager.saveDrawableToSDCard(R.drawable
				.default_project_bird_wing_up,
				project.getDirectoryInfo(),
				InstrumentationRegistry.getTargetContext())));

		soundToDelete = new SoundInfo("Sound 0", StorageManager.saveSoundResourceToSDCard(R.raw
				.default_project_tweet_1,
				project.getDirectoryInfo(),
				InstrumentationRegistry.getTargetContext()));

		sprite0.addSound(soundToDelete);

		sprite0.addSound(new SoundInfo("Sound 1", StorageManager.saveSoundResourceToSDCard(R.raw
				.default_project_tweet_1,
				project.getDirectoryInfo(),
				InstrumentationRegistry.getTargetContext())));

		sprite0.addSound(new SoundInfo("Sound 2", StorageManager.saveSoundResourceToSDCard(R.raw
				.default_project_tweet_1,
				project.getDirectoryInfo(),
				InstrumentationRegistry.getTargetContext())));

		scene0.addSprite(sprite0);
		project.addScene(scene0);

		ProjectHolder.getInstance().setCurrentProject(project);
	}
}
