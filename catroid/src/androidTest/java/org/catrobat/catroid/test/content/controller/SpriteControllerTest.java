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

package org.catrobat.catroid.test.content.controller;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
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

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.common.Constants.BACKPACK_IMAGE_DIRECTORY;
import static org.catrobat.catroid.common.Constants.BACKPACK_SOUND_DIRECTORY;
import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;
import static org.catrobat.catroid.uiespresso.util.FileTestUtils.assertFileDoesNotExist;
import static org.catrobat.catroid.uiespresso.util.FileTestUtils.assertFileDoesNotExistInDirectory;
import static org.catrobat.catroid.uiespresso.util.FileTestUtils.assertFileExists;
import static org.catrobat.catroid.uiespresso.util.FileTestUtils.assertFileExistsInDirectory;
import static org.catrobat.catroid.utils.PathBuilder.buildProjectPath;
import static org.catrobat.catroid.utils.PathBuilder.buildScenePath;

@RunWith(AndroidJUnit4.class)
public class SpriteControllerTest {

	private Project project;
	private Scene scene;
	private Sprite sprite;

	@Before
	public void setUp() throws IOException {
		clearBackPack();
		createProject();
	}

	@After
	public void tearDown() throws IOException {
		deleteProject();
		clearBackPack();
	}

	@Test
	public void testCopySprite() throws IOException {
		SpriteController controller = new SpriteController();
		Sprite copy = controller.copy(sprite, scene, scene);

		assertEquals(2, scene.getSpriteList().size());

		assertEquals(sprite.getLookList().size(), copy.getLookList().size());
		assertEquals(sprite.getSoundList().size(), copy.getSoundList().size());
		assertEquals(sprite.getNumberOfScripts(), copy.getNumberOfScripts());
		assertEquals(sprite.getNumberOfBricks(), copy.getNumberOfBricks());

		assertFileExists(copy.getLookList().get(0).getFile());
		assertFileExists(copy.getSoundList().get(0).getFile());
	}

	@Test
	public void testDeleteSprite() {
		SpriteController controller = new SpriteController();
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

		assertEquals(0, BackpackListManager.getInstance().getBackpackedSprites().size());

		assertEquals(sprite.getLookList().size(), packedSprite.getLookList().size());
		assertEquals(sprite.getSoundList().size(), packedSprite.getSoundList().size());
		assertEquals(sprite.getNumberOfScripts(), packedSprite.getNumberOfScripts());
		assertEquals(sprite.getNumberOfBricks(), packedSprite.getNumberOfBricks());

		assertFileExistsInDirectory(packedSprite.getLookList().get(0).getFile(), BACKPACK_IMAGE_DIRECTORY);
		assertFileExistsInDirectory(packedSprite.getSoundList().get(0).getFile(), BACKPACK_SOUND_DIRECTORY);
	}

	@Test
	public void testDeleteSpriteFromBackPack() throws IOException {
		SpriteController controller = new SpriteController();
		Sprite packedSprite = controller.pack(sprite);

		controller.delete(packedSprite);

		assertEquals(0, BackpackListManager.getInstance().getBackpackedSprites().size());
		assertFileDoesNotExistInDirectory(packedSprite.getLookList().get(0).getFile(), BACKPACK_IMAGE_DIRECTORY);
		assertFileDoesNotExistInDirectory(packedSprite.getSoundList().get(0).getFile(), BACKPACK_SOUND_DIRECTORY);
	}

	@Test
	public void testUnpackSprite() throws IOException {
		SpriteController controller = new SpriteController();
		Sprite packedSprite = controller.pack(sprite);
		Sprite unpackedSprite = controller.unpack(packedSprite, scene);

		assertEquals(0, BackpackListManager.getInstance().getBackpackedSprites().size());

		assertFileExistsInDirectory(packedSprite.getLookList().get(0).getFile(), BACKPACK_IMAGE_DIRECTORY);
		assertFileExistsInDirectory(packedSprite.getSoundList().get(0).getFile(), BACKPACK_SOUND_DIRECTORY);

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
		Sprite copy = controller.copy(sprite, scene, scene);

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

	private void clearBackPack() throws IOException {
		if (BACKPACK_IMAGE_DIRECTORY.exists()) {
			StorageOperations.deleteDir(BACKPACK_IMAGE_DIRECTORY);
		}
		if (BACKPACK_SOUND_DIRECTORY.exists()) {
			StorageOperations.deleteDir(BACKPACK_SOUND_DIRECTORY);
		}
		BACKPACK_IMAGE_DIRECTORY.mkdirs();
		BACKPACK_SOUND_DIRECTORY.mkdirs();
	}

	private void createProject() throws IOException {
		project = new Project(InstrumentationRegistry.getTargetContext(), "SpriteControllerTest");
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
				InstrumentationRegistry.getContext().getResources(),
				org.catrobat.catroid.test.R.raw.red_image,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				"red_image.bmp",
				1);

		sprite.getLookList().add(new LookData("testLook", imageFile));

		File soundFile = ResourceImporter.createSoundFileFromResourcesInDirectory(
				InstrumentationRegistry.getContext().getResources(),
				org.catrobat.catroid.test.R.raw.longsound,
				new File(buildScenePath(project.getName(), project.getDefaultScene().getName()), SOUND_DIRECTORY_NAME),
				"longsound.mp3");

		sprite.getSoundList().add(new SoundInfo("testSound", soundFile));

		XstreamSerializer.getInstance().saveProject(project);
	}

	private void deleteProject() throws IOException {
		File projectDir = new File(buildProjectPath(project.getName()));
		if (projectDir.exists()) {
			StorageOperations.deleteDir(projectDir);
		}
	}
}
