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

package org.catrobat.catroid.test.physics.actions;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.test.physics.PhysicsBaseTest;
import org.catrobat.catroid.test.utils.PhysicsTestUtils;
import org.catrobat.catroid.test.utils.TestUtils;

import java.io.File;

public class SetLookActionTest extends PhysicsBaseTest {

	private String multipleConvexPolygonsFileName;
	private File multipleConvexPolygonsFile;
	private static final int MULTIPLE_CONVEX_POLYGONS_RES_ID = org.catrobat.catroid.test.R.raw.multible_convex_polygons;

	private LookData lookData = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		multipleConvexPolygonsFileName = PhysicsTestUtils.getInternalImageFilenameFromFilename("multible_convex_polygons.png");

		multipleConvexPolygonsFile = TestUtils.saveFileToProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, project.getDefaultScene().getName(),
				multipleConvexPolygonsFileName, MULTIPLE_CONVEX_POLYGONS_RES_ID, getInstrumentation().getContext(),
				TestUtils.TYPE_IMAGE_FILE);

		lookData = PhysicsTestUtils.generateLookData(multipleConvexPolygonsFile);

		assertTrue("getLookData is null", sprite.look.getLookData() != null);
	}

	@Override
	protected void tearDown() throws Exception {

		multipleConvexPolygonsFileName = null;
		multipleConvexPolygonsFile = null;

		TestUtils.deleteTestProjects();
		super.tearDown();
	}

	public void testLookChanged() {

		LookData expectedLookData = lookData;
		LookData previousLookData = sprite.look.getLookData();

		changeLook();

		assertTrue("Look has not changed", sprite.look.getLookData() != previousLookData);
		assertEquals("Look is not correct", sprite.look.getLookData(), expectedLookData);
	}

	private void changeLook() {
		sprite.getLookDataList().add(lookData);
		Action action = sprite.getActionFactory().createSetLookAction(sprite, lookData);
		action.act(1.0f);
		assertNotNull("Current Look is null", sprite.look);
	}
}
