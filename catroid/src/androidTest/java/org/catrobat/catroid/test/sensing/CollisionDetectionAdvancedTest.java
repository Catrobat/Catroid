/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import android.test.InstrumentationTestCase;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.scenes.scene2d.Action;

import junit.framework.Assert;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.sensing.CollisionDetection;
import org.catrobat.catroid.sensing.CollisionInformation;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.Utils;

import java.io.File;

public class CollisionDetectionAdvancedTest extends InstrumentationTestCase {
	protected Project project;
	protected Sprite sprite1;
	protected Sprite sprite2;

	protected static LookData generateLookData(File testImage) {
		LookData lookData = new LookData();
		lookData.setLookFilename(testImage.getName());
		lookData.setLookName(testImage.getName());
		Pixmap pixmap = Utils.getPixmapFromFile(testImage);
		lookData.setPixmap(pixmap);
		return lookData;
	}

	protected void initializeSprite(Sprite sprite, int resourceId, String filename) {
		sprite.look = new Look(sprite);
		sprite.setActionFactory(new ActionFactory());

		String hashedFileName = Utils.md5Checksum(filename) + "_" + filename;
		File file = null;

		try {
			file = TestUtils.saveFileToProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, project.getDefaultScene().getName(),
					hashedFileName, resourceId, getInstrumentation().getContext(),
					TestUtils.TYPE_IMAGE_FILE);
		} catch (Exception e) {
			Assert.fail("Couldn't load file, exception thrown!");
		}

		LookData lookData = generateLookData(file);
		CollisionInformation collisionInformation = lookData.getCollisionInformation();
		collisionInformation.loadOrCreateCollisionPolygon();

		sprite.look.setLookData(lookData);
		sprite.addLookData(lookData);
		sprite.look.setHeight(sprite.look.getLookData().getPixmap().getHeight());
		sprite.look.setWidth(sprite.look.getLookData().getPixmap().getWidth());
		sprite.look.setPositionInUserInterfaceDimensionUnit(0, 0);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtils.deleteTestProjects();

		project = new Project(getInstrumentation().getTargetContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);

		sprite1 = new Sprite("TestSprite1");
		sprite2 = new Sprite("TestSprite2");

		project.getDefaultScene().addSprite(sprite1);
		project.getDefaultScene().addSprite(sprite2);

		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		initializeSprite(sprite1, org.catrobat.catroid.test.R.raw.collision_donut, "collision_donut.png");
		initializeSprite(sprite2, org.catrobat.catroid.test.R.raw.icon, "icon.png");

		Polygon[] collisionPolygons1 = sprite1.look.getLookData().getCollisionInformation().collisionPolygons;
		Polygon[] collisionPolygons2 = sprite2.look.getLookData().getCollisionInformation().collisionPolygons;

		Assert.assertNotNull("CollsionPolygons is null", collisionPolygons1);
		Assert.assertEquals("Wrong amount of collisionPolygons", 2, collisionPolygons1.length);

		Assert.assertNotNull("CollsionPolygons is null", collisionPolygons2);
		Assert.assertEquals("Wrong amount of collisionPolygons", 3, collisionPolygons2.length);

		StorageHandler.getInstance().saveProject(project);
	}

	public void testCollisionBetweenMovingLooks() {
		boolean colliding = CollisionDetection.checkCollisionBetweenLooks(sprite1.look, sprite2.look) != 0;
		Assert.assertFalse("Looks wrongly detected as colliding", colliding);

		float steps = 200.0f;
		ActionFactory factory = new ActionFactory();
		sprite2.setActionFactory(factory);
		Action moveNSteptsaction = factory.createMoveNStepsAction(sprite2, new Formula(steps));
		moveNSteptsaction.act(1.0f);

		colliding = CollisionDetection.checkCollisionBetweenLooks(sprite1.look, sprite2.look) != 0;

		Assert.assertTrue("Looks wrongly detected as not colliding", colliding);
	}

	public void testCollisionBetweenExpandingLooks() {
		boolean colliding = CollisionDetection.checkCollisionBetweenLooks(sprite1.look, sprite2.look) != 0;

		Assert.assertFalse("Looks wrongly detected as colliding", colliding);

		float size = 300.0f;
		ActionFactory factory = new ActionFactory();
		sprite2.setActionFactory(factory);
		Action createChangeSizeByNAction = factory.createChangeSizeByNAction(sprite2, new Formula(size));
		createChangeSizeByNAction.act(1.0f);

		colliding = CollisionDetection.checkCollisionBetweenLooks(sprite1.look, sprite2.look) != 0;

		Assert.assertTrue("Looks wrongly detected as colliding", colliding);
	}
}
