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

package org.catrobat.catroid.test.physics.actions;

import android.support.test.InstrumentationRegistry;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.test.physics.PhysicsBaseTest;
import org.catrobat.catroid.test.utils.PhysicsTestUtils;
import org.catrobat.catroid.test.utils.TestUtils;

import java.io.File;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

public class SetLookActionTest extends PhysicsBaseTest {

	private String multipleConvexPolygonsFileName;
	private File multipleConvexPolygonsFile;

	private LookData lookData = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		multipleConvexPolygonsFileName = PhysicsTestUtils.getInternalImageFilenameFromFilename("multible_convex_polygons.png");

		multipleConvexPolygonsFile = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getContext().getResources(),
				org.catrobat.catroid.test.R.raw.multible_convex_polygons,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				multipleConvexPolygonsFileName,
				1);

		lookData = PhysicsTestUtils.generateLookData(multipleConvexPolygonsFile);

		assertNotNull(sprite.look.getLookData());
	}

	@Override
	protected void tearDown() throws Exception {

		multipleConvexPolygonsFileName = null;
		multipleConvexPolygonsFile = null;

		TestUtils.deleteProjects();
		super.tearDown();
	}

	public void testLookChanged() {

		LookData expectedLookData = lookData;
		LookData previousLookData = sprite.look.getLookData();

		changeLook();

		assertThat(sprite.look.getLookData(), is(not(previousLookData)));
		assertEquals(sprite.look.getLookData(), expectedLookData);
	}

	private void changeLook() {
		sprite.getLookList().add(lookData);
		Action action = sprite.getActionFactory().createSetLookAction(sprite, lookData);
		action.act(1.0f);
		assertNotNull("Current Look is null", sprite.look);
	}
}
