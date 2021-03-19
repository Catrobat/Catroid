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

package org.catrobat.catroid.test.physics.collision;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.sensing.CollisionDetection;
import org.catrobat.catroid.sensing.CollisionInformation;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.test.physics.PhysicsTestUtils.generateLookData;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class CollisionDetectionAdvancedTest {
	protected Project project;
	protected Sprite sprite1;
	protected Sprite sprite2;

	private void initializeSprite(Sprite sprite, int resourceId, String filename) throws IOException {
		sprite.look = new Look(sprite);
		sprite.setActionFactory(new ActionFactory());

		String hashedFileName = Utils.md5Checksum(filename) + "_" + filename;

		File file = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				resourceId,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				hashedFileName,
				1);

		LookData lookData = generateLookData(file);
		CollisionInformation collisionInformation = lookData.getCollisionInformation();
		collisionInformation.loadCollisionPolygon();

		sprite.look.setLookData(lookData);
		sprite.getLookList().add(lookData);
		sprite.look.setHeight(sprite.look.getLookData().getPixmap().getHeight());
		sprite.look.setWidth(sprite.look.getLookData().getPixmap().getWidth());
		sprite.look.setPositionInUserInterfaceDimensionUnit(0, 0);
	}

	@Before
	public void setUp() throws Exception {
		TestUtils.deleteProjects();

		project = new Project(ApplicationProvider.getApplicationContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);

		sprite1 = new Sprite("TestSprite1");
		sprite2 = new Sprite("TestSprite2");

		project.getDefaultScene().addSprite(sprite1);
		project.getDefaultScene().addSprite(sprite2);

		XstreamSerializer.getInstance().saveProject(project);
		ProjectManager.getInstance().setCurrentProject(project);

		initializeSprite(sprite1, org.catrobat.catroid.test.R.raw.collision_donut, "collision_donut.png");
		initializeSprite(sprite2, org.catrobat.catroid.test.R.raw.icon, "icon.png");

		Polygon[] collisionPolygons1 = sprite1.look.getLookData().getCollisionInformation().collisionPolygons;
		Polygon[] collisionPolygons2 = sprite2.look.getLookData().getCollisionInformation().collisionPolygons;

		assertNotNull(collisionPolygons1);
		assertEquals(2, collisionPolygons1.length);

		assertNotNull(collisionPolygons2);
		assertEquals(3, collisionPolygons2.length);

		XstreamSerializer.getInstance().saveProject(project);
	}

	@Test
	public void testCollisionBetweenMovingLooks() {
		assertFalse(CollisionDetection.checkCollisionBetweenLooks(sprite1.look, sprite2.look));

		float steps = 200.0f;
		ActionFactory factory = new ActionFactory();
		sprite2.setActionFactory(factory);
		Action moveNSteptsaction = factory.createMoveNStepsAction(sprite2, new SequenceAction(), new Formula(steps));
		moveNSteptsaction.act(1.0f);

		assertTrue(CollisionDetection.checkCollisionBetweenLooks(sprite1.look, sprite2.look));
	}

	@Test
	public void testCollisionBetweenExpandingLooks() {
		assertFalse(CollisionDetection.checkCollisionBetweenLooks(sprite1.look, sprite2.look));

		float size = 300.0f;
		ActionFactory factory = new ActionFactory();
		sprite2.setActionFactory(factory);
		Action createChangeSizeByNAction = factory.createChangeSizeByNAction(sprite2, new SequenceAction(), new Formula(size));
		createChangeSizeByNAction.act(1.0f);

		assertTrue(CollisionDetection.checkCollisionBetweenLooks(sprite1.look, sprite2.look));
	}
}
