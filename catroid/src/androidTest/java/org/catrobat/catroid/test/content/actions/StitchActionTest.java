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

package org.catrobat.catroid.test.content.actions;

import android.graphics.PointF;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.embroidery.EmbroideryManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class StitchActionTest {

	private Sprite testSprite;
	private Project project;
	private float xCoord = 50.0f;
	private float yCoord = 160.0f;

	@Before
	public void setUp() throws Exception {
		testSprite = new Sprite("testSprite");
		testSprite.look.setX(xCoord);
		testSprite.look.setY(yCoord);
		project = new Project(InstrumentationRegistry.getTargetContext(), "testProject");
		ProjectManager.getInstance().setProject(project);
	}

	@Test
	public void testAddStitchPoint() {
		int expectedStitchCount = 1;
		testSprite.getActionFactory().createStitchAction(testSprite).act(1f);

		assertEquals(expectedStitchCount, EmbroideryManager.getInstance().getStitchPoints().size());

		int firstPoint = 0;
		PointF point = EmbroideryManager.getInstance().getStitchPoints().get(firstPoint);
		assertEquals(xCoord, point.x);
		assertEquals(yCoord, point.y);
	}
}
