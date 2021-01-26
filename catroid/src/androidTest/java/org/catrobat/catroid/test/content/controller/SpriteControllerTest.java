/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

package org.catrobat.catroid.test.content.controller;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;
import static org.catrobat.catroid.test.utils.TestUtils.clearBackPack;
import static org.catrobat.catroid.uiespresso.util.FileTestUtils.assertFileDoesNotExist;
import static org.catrobat.catroid.uiespresso.util.FileTestUtils.assertFileDoesNotExistInDirectory;
import static org.catrobat.catroid.uiespresso.util.FileTestUtils.assertFileExists;
import static org.catrobat.catroid.uiespresso.util.FileTestUtils.assertFileExistsInDirectory;

@RunWith(AndroidJUnit4.class)
public class SpriteControllerTest {

	private Project project;
	private Scene scene;
	private Sprite sprite;
	private BackpackListManager backpackListManager;

	@Before
	public void setUp() throws IOException {
		backpackListManager = BackpackListManager.getInstance();
		clearBackPack(backpackListManager);
		createProject();
	}

	@After
	public void tearDown() throws IOException {
		deleteProject();
		clearBackPack(backpackListManager);
	}

	@Test
	public void testCopySprite() throws IOException {
		SpriteController controller = new SpriteController();

		String spriteVarName = "spriteVar";
		String spriteListName = "spriteList";
		assertTrue(sprite.addUserVariable(new UserVariable(spriteVarName)));
		assertTrue(sprite.addUserList(new UserList(spriteListName)));

		Sprite copy = controller.copy(sprite, project, scene);

		assertEquals(2, scene.getSpriteList().size());

		assertEquals(sprite.getLookList().size(), copy.getLookList().size());
		assertEquals(sprite.getSoundList().size(), copy.getSoundList().size());
		assertEquals(sprite.getNumberOfScripts(), copy.getNumberOfScripts());
		assertEquals(sprite.getNumberOfBricks(), copy.getNumberOfBricks());

		assertNotNull(sprite.getUserVariable(spriteVarName));
		assertNotNull(copy.getUserVariable(spriteVarName));
		assertNotSame(sprite.getUserVariable(spriteVarName),
				copy.getUserVariable(spriteVarName));

		assertNotNull(sprite.getUserList(spriteListName));
		assertNotNull(copy.getUserList(spriteListName));
		assertNotSame(sprite.getUserList(spriteListName),
				copy.getUserList(spriteListName));

		assertFileExists(copy.getLookList().get(0).getFile());
		assertFileExists(copy.getSoundList().get(0).getFile());
	}

	@Test
	public void testDeleteSprite() {
		SpriteController controller = new SpriteController();

		String spriteVarName = "spriteVar";
		String spriteListName = "spriteList";
		assertTrue(sprite.addUserVariable(new UserVariable(spriteVarName)));
		assertTrue(sprite.addUserList(new UserList(spriteListName)));

		File deletedLookFile = sprite.getLookList().get(0).getFile();
		File deletedSoundFile = sprite.getSoundList().get(0).getFile();

		controller.delete(sprite);

		assertEquals(2, scene.getSpriteList().size());

		assertFileDoesNotExist(deletedLookFile);
		assertFileDoesNotExist(deletedSoundFile);
	}

	@Test
	public void testPackSprite() throws IOException {
		SpriteController controller = new SpriteController();
		Sprite packedSprite = controller.pack(sprite);

		assertEquals(0, backpackListManager.getSprites().size());

		assertEquals(sprite.getLookList().size(), packedSprite.getLookList().size());
		assertEquals(sprite.getSoundList().size(), packedSprite.getSoundList().size());
		assertEquals(sprite.getNumberOfScripts(), packedSprite.getNumberOfScripts());
		assertEquals(sprite.getNumberOfBricks(), packedSprite.getNumberOfBricks());

		assertFileExistsInDirectory(packedSprite.getLookList().get(0).getFile(),
				backpackListManager.backpackImageDirectory);
		assertFileExistsInDirectory(packedSprite.getSoundList().get(0).getFile(),
				backpackListManager.backpackSoundDirectory);
	}

	@Test
	public void testDeleteSpriteFromBackPack() throws IOException {
		SpriteController controller = new SpriteController();
		Sprite packedSprite = controller.pack(sprite);

		controller.delete(packedSprite);

		assertEquals(0, BackpackListManager.getInstance().getSprites().size());
		assertFileDoesNotExistInDirectory(packedSprite.getLookList().get(0).getFile(),
				backpackListManager.backpackImageDirectory);
		assertFileDoesNotExistInDirectory(packedSprite.getSoundList().get(0).getFile(),
				backpackListManager.backpackSoundDirectory);
	}

	@Test
	public void testUnpackSprite() throws IOException {
		SpriteController controller = new SpriteController();
		Sprite packedSprite = controller.pack(sprite);
		Sprite unpackedSprite = controller.unpack(packedSprite, project, scene);

		assertEquals(0, BackpackListManager.getInstance().getSprites().size());

		assertFileExistsInDirectory(packedSprite.getLookList().get(0).getFile(),
				backpackListManager.backpackImageDirectory);
		assertFileExistsInDirectory(packedSprite.getSoundList().get(0).getFile(),
				backpackListManager.backpackSoundDirectory);

		assertEquals(2, scene.getSpriteList().size());

		assertEquals(sprite.getLookList().size(), unpackedSprite.getLookList().size());
		assertEquals(sprite.getSoundList().size(), unpackedSprite.getSoundList().size());
		assertEquals(sprite.getNumberOfScripts(), unpackedSprite.getNumberOfScripts());
		assertEquals(sprite.getNumberOfBricks(), unpackedSprite.getNumberOfBricks());

		assertFileExists(unpackedSprite.getLookList().get(0).getFile());
		assertFileExists(unpackedSprite.getSoundList().get(0).getFile());
	}

	@Test
	public void testDeepCopySprite() throws IOException {
		SpriteController controller = new SpriteController();
		Sprite copy = controller.copy(sprite, project, scene);

		assertEquals(2, scene.getSpriteList().size());

		assertEquals(sprite.getLookList().size(), copy.getLookList().size());
		assertEquals(sprite.getSoundList().size(), copy.getSoundList().size());
		assertEquals(sprite.getNumberOfScripts(), copy.getNumberOfScripts());
		assertEquals(sprite.getNumberOfBricks(), copy.getNumberOfBricks());

		assertFileExists(copy.getLookList().get(0).getFile());
		assertFileExists(copy.getSoundList().get(0).getFile());

		controller.delete(sprite);

		assertEquals(sprite.getLookList().size(), copy.getLookList().size());
		assertEquals(sprite.getSoundList().size(), copy.getSoundList().size());
		assertEquals(sprite.getNumberOfScripts(), copy.getNumberOfScripts());
		assertEquals(sprite.getNumberOfBricks(), copy.getNumberOfBricks());

		assertFileExists(copy.getLookList().get(0).getFile());
		assertFileExists(copy.getSoundList().get(0).getFile());

		assertFileDoesNotExist(sprite.getLookList().get(0).getFile());
		assertFileDoesNotExist(sprite.getSoundList().get(0).getFile());
	}

	private void createProject() throws IOException {
		project = new Project(ApplicationProvider.getApplicationContext(), "SpriteControllerTest");
		scene = project.getDefaultScene();
		ProjectManager.getInstance().setCurrentProject(project);

		sprite = new Sprite("testSprite");
		scene.addSprite(sprite);

		StartScript script = new StartScript();
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(0, 0);
		script.addBrick(placeAtBrick);
		sprite.addScript(script);

		XstreamSerializer.getInstance().saveProject(project);

		File imageFile = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.raw.red_image,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				"red_image.bmp",
				1);

		sprite.getLookList().add(new LookData("testLook", imageFile));

		File soundFile = ResourceImporter.createSoundFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.raw.longsound,
				new File(project.getDefaultScene().getDirectory(), SOUND_DIRECTORY_NAME),
				"longsound.mp3");

		sprite.getSoundList().add(new SoundInfo("testSound", soundFile));
		XstreamSerializer.getInstance().saveProject(project);
	}

	private void deleteProject() throws IOException {
		if (project.getDirectory().exists()) {
			StorageOperations.deleteDir(project.getDirectory());
		}
	}
}
