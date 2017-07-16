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

package org.catrobat.catroid.uiespresso.gui.listfragment;

import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;

import org.catrobat.catroid.R;
import org.catrobat.catroid.copypaste.ClipboardHandler;
import org.catrobat.catroid.data.ProjectInfo;
import org.catrobat.catroid.data.SceneInfo;
import org.catrobat.catroid.data.SpriteInfo;
import org.catrobat.catroid.gui.activity.SpriteListActivity;
import org.catrobat.catroid.gui.fragment.SceneListFragment;
import org.catrobat.catroid.projecthandler.ProjectHolder;
import org.catrobat.catroid.storage.DirectoryPathInfo;
import org.catrobat.catroid.uiespresso.pocketmusic.RecyclerViewMatcher;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

public class SpriteListTest {

	private SceneInfo scene;
	private SpriteInfo spriteToRename;
	private SpriteInfo spriteToDelete;

	@Rule
	public BaseActivityInstrumentationRule<SpriteListActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteListActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject();
		Intent intent = new Intent();
		intent.putExtra(SceneListFragment.SELECTED_SCENE, "Scene 0");
		baseActivityTestRule.launchActivity(intent);
	}

	private RecyclerViewMatcher getRecyclerViewWithId(int id) {
		return new RecyclerViewMatcher(id);
	}

	@Test
	public void testBackgroundSprite() {
		onView(getRecyclerViewWithId(R.id.fragment).atPosition(0))
				.check(matches(hasDescendant(withText(R.string.background))));

		onView(withId(R.id.fragment))
				.perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));
		onView(withId(R.id.btnEdit)).check(doesNotExist());

		onView(withText(R.string.background)).check(matches(isDisplayed()));
	}

	@Test
	public void testRename() {
		String newName = "New Name";
		onView(withId(R.id.fragment))
				.perform(RecyclerViewActions.actionOnItemAtPosition(1, longClick()));
		onView(withId(R.id.btnEdit)).perform(click());

		onView(withText(R.string.rename_sprite_dialog)).check(matches(isDisplayed()));

		onView(withId(R.id.edit_text)).perform(replaceText(newName));
		onView(withText(R.string.ok)).perform(click());

		assertEquals(newName, spriteToRename.getName());
	}

	@Test
	public void testDelete() {
		onView(withId(R.id.fragment))
				.perform(RecyclerViewActions.actionOnItemAtPosition(2, longClick()));
		onView(withId(R.id.btnDelete)).perform(click());

		assertFalse(scene.getSprites().contains(spriteToDelete));
	}

	@Test
	public void testCopyAndPaste() throws Exception {
		onView(withId(R.id.fragment))
				.perform(RecyclerViewActions.actionOnItemAtPosition(3, longClick()));
		onView(withId(R.id.btnCopy)).perform(click());

		assertEquals(1, ClipboardHandler.getClipboard().getClipboardSize());
		onView(withId(R.id.btnPaste)).check(matches(isDisplayed()));

		int numberOfItems = scene.getSprites().size();

		onView(withId(R.id.btnPaste)).perform(click());
		assertEquals(numberOfItems + 1, scene.getSprites().size());
	}

	private void createProject() throws Exception {
		ProjectInfo project = new ProjectInfo("Test");

		scene = new SceneInfo("Scene 0", new DirectoryPathInfo(project.getDirectoryInfo(), ""));
		SpriteInfo sprite0 = new SpriteInfo("Sprite 0", new DirectoryPathInfo(project.getDirectoryInfo(), ""));
		SpriteInfo sprite1 = new SpriteInfo("Sprite 1", new DirectoryPathInfo(project.getDirectoryInfo(), ""));
		SpriteInfo sprite2 = new SpriteInfo("Sprite 2", new DirectoryPathInfo(project.getDirectoryInfo(), ""));
		SpriteInfo sprite3 = new SpriteInfo("Sprite 3", new DirectoryPathInfo(project.getDirectoryInfo(), ""));

		scene.addSprite(sprite0);
		scene.addSprite(sprite1);
		scene.addSprite(sprite2);
		scene.addSprite(sprite3);

		project.addScene(scene);

		ProjectHolder.getInstance().setCurrentProject(project);

		spriteToRename = sprite1;
		spriteToDelete = sprite2;
	}
}
