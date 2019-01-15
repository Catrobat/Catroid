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

package org.catrobat.catroid.test.sensing;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.sensing.CollisionDetection;
import org.catrobat.catroid.test.physics.collision.CollisionTestUtils;
import org.catrobat.catroid.test.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class TouchesEdgeTest {
	protected Project project;
	protected Sprite sprite1;

	@Before
	public void setUp() throws Exception {
		TestUtils.deleteProjects();

		project = new Project(InstrumentationRegistry.getTargetContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);
		sprite1 = new Sprite("TestSprite1");
		project.getDefaultScene().addSprite(sprite1);

		XstreamSerializer.getInstance().saveProject(project);
		ProjectManager.getInstance().setCurrentProject(project);

		CollisionTestUtils.initializeSprite(sprite1, org.catrobat.catroid.test.R.raw.collision_donut,
				"collision_donut.png", InstrumentationRegistry.getContext(), project);
	}

	@Test
	public void testCollisionWithRightEdge() {
		sprite1.look.setXInUserInterfaceDimensionUnit(0);
		sprite1.look.setYInUserInterfaceDimensionUnit(0);
		assertThat(CollisionDetection.collidesWithEdge(sprite1.look), is(not(equalTo(1d))));
		int virtualScreenWidth = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenWidth;
		sprite1.look.setXInUserInterfaceDimensionUnit(sprite1.look.getXInUserInterfaceDimensionUnit()
				+ virtualScreenWidth / 2);
		assertEquals(1d, CollisionDetection.collidesWithEdge(sprite1.look));
	}

	@Test
	public void testCollisionWithLeftEdge() {
		sprite1.look.setXInUserInterfaceDimensionUnit(0);
		sprite1.look.setYInUserInterfaceDimensionUnit(0);
		assertThat(CollisionDetection.collidesWithEdge(sprite1.look), is(not(equalTo(1d))));
		int virtualScreenWidth = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenWidth;
		sprite1.look.setXInUserInterfaceDimensionUnit(sprite1.look.getXInUserInterfaceDimensionUnit()
				- virtualScreenWidth / 2);
		assertEquals(1d, CollisionDetection.collidesWithEdge(sprite1.look));
	}

	@Test
	public void testCollisionWithUpperEdge() {
		sprite1.look.setXInUserInterfaceDimensionUnit(0);
		sprite1.look.setYInUserInterfaceDimensionUnit(0);
		assertThat(CollisionDetection.collidesWithEdge(sprite1.look), is(not(equalTo(1d))));
		int virtualScreenHeight = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenHeight;
		sprite1.look.setYInUserInterfaceDimensionUnit(sprite1.look.getXInUserInterfaceDimensionUnit()
				+ virtualScreenHeight / 2);
		assertEquals(1d, CollisionDetection.collidesWithEdge(sprite1.look));
	}

	@Test
	public void testCollisionWithBottomEdge() {
		sprite1.look.setXInUserInterfaceDimensionUnit(0);
		sprite1.look.setYInUserInterfaceDimensionUnit(0);
		assertThat(CollisionDetection.collidesWithEdge(sprite1.look), is(not(equalTo(1d))));
		int virtualScreenHeight = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenHeight;
		sprite1.look.setYInUserInterfaceDimensionUnit(sprite1.look.getXInUserInterfaceDimensionUnit()
				- virtualScreenHeight / 2);
		assertEquals(1d, CollisionDetection.collidesWithEdge(sprite1.look));
	}
}
