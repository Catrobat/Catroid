/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.test.AndroidTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory;

import java.util.List;

public class CategoryBricksFactoryTest extends AndroidTestCase {

	private final CategoryBricksFactory factory = new CategoryBricksFactory();
	private Sprite background;
	private Sprite sprite = new Sprite("newSprite");
	private Context context = getContext();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = getContext();

		SettingsActivity.resetSharedPreferences(context);

		Project project = new Project(context, "Project");
		background = project.getSpriteList().get(0);
		project.addSprite(sprite);
		ProjectManager.getInstance().setProject(project);
	}

	public void testControlBricks() {
		final int expectedBrickCount = 10;
		checkBrickCountInCategory(R.string.category_control, background, expectedBrickCount);
		checkBrickCountInCategory(R.string.category_control, sprite, expectedBrickCount);
	}

	public void testMotionBricks() {
		final int expectedBackgroundBrickCount = 12;
		checkBrickCountInCategory(R.string.category_motion, background, expectedBackgroundBrickCount);
		final int expectedSpriteBrickCount = 15;
		checkBrickCountInCategory(R.string.category_motion, sprite, expectedSpriteBrickCount);
	}

	public void testSoundBricks() {
		final int expectedBrickCount = 5;
		checkBrickCountInCategory(R.string.category_sound, background, expectedBrickCount);
		checkBrickCountInCategory(R.string.category_sound, sprite, expectedBrickCount);
	}

	public void testLooksBricks() {
		final int expectedBrickCount = 13;
		checkBrickCountInCategory(R.string.category_looks, background, expectedBrickCount);
		checkBrickCountInCategory(R.string.category_looks, sprite, expectedBrickCount);
	}

	public void testDataBricks() {
		final int expectedBrickCount = 8;
		checkBrickCountInCategory(R.string.category_data, background, expectedBrickCount);
		checkBrickCountInCategory(R.string.category_data, sprite, expectedBrickCount);
	}

	public void testLegoNxtBricks() {
		final int expectedBrickCount = 4;
		checkBrickCountInCategory(R.string.category_lego_nxt, background, expectedBrickCount);
		checkBrickCountInCategory(R.string.category_lego_nxt, sprite, expectedBrickCount);
	}

	public void testUnknownCategory() {
		List<Brick> bricks = factory.getBricks("NON_EXISTING_CATEGORY", sprite, context);
		assertTrue("Non existing category is not empty", bricks.isEmpty());
	}

	private void checkBrickCountInCategory(int categoryId, Sprite sprite, int expectedBrickCount) {
		List<Brick> bricks = factory.getBricks(context.getString(categoryId), sprite, context);
		assertEquals("Wrong bricks count in motion category", expectedBrickCount, bricks.size());
	}
}
