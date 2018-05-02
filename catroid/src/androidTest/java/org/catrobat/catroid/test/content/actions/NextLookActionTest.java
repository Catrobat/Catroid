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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.test.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;

@RunWith(AndroidJUnit4.class)
public class NextLookActionTest {

	private final String projectName = "testProject";

	private File projectDir;
	private File testImage;

	@Before
	public void setUp() throws Exception {
		projectDir = new File(Constants.DEFAULT_ROOT_DIRECTORY, projectName);

		if (projectDir.exists()) {
			StorageOperations.deleteDir(projectDir);
		}

		createProject();
	}

	@After
	public void tearDown() throws Exception {
		if (projectDir.exists()) {
			StorageOperations.deleteDir(projectDir);
		}
	}

	@Test
	public void testNextLook() {
		Sprite sprite = new SingleSprite("cat");

		LookData lookData1 = new LookData();
		lookData1.setFileName(testImage.getName());
		lookData1.setName("testImage1");
		sprite.getLookList().add(lookData1);

		LookData lookData2 = new LookData();
		lookData2.setFileName(testImage.getName());
		lookData2.setName("testImage2");
		sprite.getLookList().add(lookData2);

		ActionFactory factory = sprite.getActionFactory();
		Action setLookAction = factory.createSetLookAction(sprite, lookData1);
		Action nextLookAction = factory.createNextLookAction(sprite);

		setLookAction.act(1.0f);
		nextLookAction.act(1.0f);

		assertEquals(lookData2, sprite.look.getLookData());
	}

	@Test
	public void testLastLook() {
		Sprite sprite = new SingleSprite("cat");

		LookData lookData1 = new LookData();
		lookData1.setFileName(testImage.getName());
		lookData1.setName("testImage1");
		lookData1.setFileName("testImage1");
		sprite.getLookList().add(lookData1);

		LookData lookData2 = new LookData();
		lookData2.setFileName(testImage.getName());
		lookData2.setName("testImage");
		lookData2.setFileName("testImage2");
		sprite.getLookList().add(lookData2);

		LookData lookData3 = new LookData();
		lookData3.setFileName(testImage.getName());
		lookData3.setName("testImage");
		lookData3.setFileName("testImage3");
		sprite.getLookList().add(lookData3);

		ActionFactory factory = sprite.getActionFactory();
		Action setLookAction = factory.createSetLookAction(sprite, lookData3);
		Action nextLookAction = factory.createNextLookAction(sprite);

		setLookAction.act(1.0f);
		nextLookAction.act(1.0f);

		assertEquals(lookData1, sprite.look.getLookData());
	}

	@Test
	public void testLookGalleryNull() {

		Sprite sprite = new SingleSprite("cat");
		ActionFactory factory = sprite.getActionFactory();
		Action nextLookAction = factory.createNextLookAction(sprite);
		nextLookAction.act(1.0f);

		assertNull(sprite.look.getLookData());
	}

	@Test
	public void testLookGalleryWithOneLook() {
		Sprite sprite = new SingleSprite("cat");

		LookData lookData1 = new LookData();
		lookData1.setFileName(testImage.getName());
		lookData1.setName("testImage1");
		sprite.getLookList().add(lookData1);

		ActionFactory factory = sprite.getActionFactory();
		Action setLookAction = factory.createSetLookAction(sprite, lookData1);
		Action nextLookAction = factory.createNextLookAction(sprite);

		setLookAction.act(1.0f);
		nextLookAction.act(1.0f);

		assertEquals(lookData1, sprite.look.getLookData());
	}

	@Test
	public void testNextLookWithNoLookSet() {

		Sprite sprite = new SingleSprite("cat");

		ActionFactory factory = sprite.getActionFactory();
		Action nextLookAction = factory.createNextLookAction(sprite);

		LookData lookData1 = new LookData();
		lookData1.setFileName(testImage.getName());
		lookData1.setName("testImage1");
		sprite.getLookList().add(lookData1);

		nextLookAction.act(1.0f);

		assertNull(sprite.look.getLookData());
	}

	private void createProject() throws IOException {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		Sprite firstSprite = new SingleSprite("cat");
		Script testScript = new StartScript();

		firstSprite.addScript(testScript);
		project.getDefaultScene().addSprite(firstSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);

		XstreamSerializer.getInstance().saveProject(project);

		testImage = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getContext().getResources(),
				R.raw.icon, new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				"testImage" + ".png", 1);

		ScreenValues.SCREEN_HEIGHT = 200;
		ScreenValues.SCREEN_WIDTH = 200;
	}
}
