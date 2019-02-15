/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.test.ui;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class CategoryBricksFactoryTest {

	private final CategoryBricksFactory factory = new CategoryBricksFactory();
	private Sprite background;
	private Sprite sprite = new SingleSprite("newSprite");
	private Context context = InstrumentationRegistry.getContext();

	@Before
	public void setUp() throws Exception {
		context = InstrumentationRegistry.getTargetContext();

		SettingsFragment.resetSharedPreferences(context);

		Project project = new Project(context, "Project");
		background = project.getDefaultScene().getSpriteList().get(0);
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
	}

	@Test
	public void testEventBrick() {
		final int expectedBrickCount = 10;
		checkBrickCountInCategory(R.string.category_event, background, expectedBrickCount);
		checkBrickCountInCategory(R.string.category_event, sprite, expectedBrickCount);
	}

	@Test
	public void testControlBricks() {
		final int expectedBrickCount = 14;
		checkBrickCountInCategory(R.string.category_control, background, expectedBrickCount);
		checkBrickCountInCategory(R.string.category_control, sprite, expectedBrickCount);
	}

	@Test
	public void testMotionBricks() {
		final int expectedBackgroundBrickCount = 22;
		checkBrickCountInCategory(R.string.category_motion, background, expectedBackgroundBrickCount);
		final int expectedSpriteBrickCount = 25;
		checkBrickCountInCategory(R.string.category_motion, sprite, expectedSpriteBrickCount);
	}

	@Test
	public void testSoundBricks() {
		final int expectedBrickCount = 8;
		checkBrickCountInCategory(R.string.category_sound, background, expectedBrickCount);
		checkBrickCountInCategory(R.string.category_sound, sprite, expectedBrickCount);
	}

	@Test
	public void testLooksBricks() {
		final int expectedBackgroundBrickCount = 22;
		checkBrickCountInCategory(R.string.category_looks, background, expectedBackgroundBrickCount);
		final int expectedBrickCount = 28;
		checkBrickCountInCategory(R.string.category_looks, sprite, expectedBrickCount);
	}

	@Test
	public void testPenBricks() {
		final int expectedBackgroundBrickCount = 1;
		checkBrickCountInCategory(R.string.category_pen, background, expectedBackgroundBrickCount);
		final int expectedSpriteBrickCount = 6;
		checkBrickCountInCategory(R.string.category_pen, sprite, expectedSpriteBrickCount);
	}

	@Test
	public void testDataBricks() {
		final int expectedBrickCount = 10;
		checkBrickCountInCategory(R.string.category_data, background, expectedBrickCount);
		checkBrickCountInCategory(R.string.category_data, sprite, expectedBrickCount);
	}

	@Test
	public void testLegoNxtBricks() {
		final int expectedBrickCount = 4;
		checkBrickCountInCategory(R.string.category_lego_nxt, background, expectedBrickCount);
		checkBrickCountInCategory(R.string.category_lego_nxt, sprite, expectedBrickCount);
	}

	@Test
	public void testUnknownCategory() {
		List<Brick> bricks = factory.getBricks("NON_EXISTING_CATEGORY", sprite, context);
		assertTrue(bricks.isEmpty());
	}

	private void checkBrickCountInCategory(int categoryId, Sprite sprite, int expectedBrickCount) {
		List<Brick> bricks = factory.getBricks(context.getString(categoryId), sprite, context);
		assertEquals(String.format("Wrong bricks count in %s category", context.getString(categoryId)), expectedBrickCount, bricks.size());
	}
}
